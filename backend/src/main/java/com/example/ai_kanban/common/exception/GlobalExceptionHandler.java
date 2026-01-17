package com.example.ai_kanban.common.exception;

import com.example.ai_kanban.common.ApiResponse;
import com.example.ai_kanban.common.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;

/**
 * 全局异常处理，确保所有错误以标准的 JSON 格式返回，并显式设置 HTTP 状态码，
 * 彻底防止默认的视图/转发机制触发路径循环。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. 处理业务系统异常 (SystemException)
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 业务错误通常返回 400
    public ApiResponse<String> handleSystemExceptions(SystemException se){
        logger.warn("System Exception caught: Code={}, Message={}", se.getCode(), se.getMessage());
        return ApiResponse.fail(se.getCode(), se.getMessage());
    }

    // 2. 处理 Spring Security 认证异常 (AuthenticationException)
    // 拦截 authenticationManager.authenticate() 抛出的异常
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 认证失败统一返回 401
    public ApiResponse<String> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());

        if (ex instanceof BadCredentialsException) {
            return ApiResponse.fail(ResultCode.LOGIN_ERROR.getCode(), "用户名或密码错误");
        } else if (ex instanceof LockedException) {
            return ApiResponse.fail(ResultCode.USER_DISABLED.getCode(), "账户已被锁定");
        } else if (ex instanceof DisabledException) {
            return ApiResponse.fail(ResultCode.USER_DISABLED.getCode(), "账户已被禁用");
        }

        return ApiResponse.fail(ResultCode.UNAUTHORIZED.getCode(), "认证失败: " + ex.getMessage());
    }

    // 3. 处理参数校验失败 (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 参数校验失败返回 400
    public ApiResponse<String> handleIllegalArgumentException(MethodArgumentNotValidException ex){
        String message= Optional.of(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("参数校验失败");

        logger.warn("Validation Error: {}", message);
        // 使用 ResultCode.VALIDATION_ERROR 提供的错误码
        return ApiResponse.fail(ResultCode.VALIDATION_ERROR.getCode(), message);
    }

    // 4. @Validated 参数校验异常（GET/PathVariable）
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleConstraintViolation(ConstraintViolationException ex){
        logger.warn("Constraint Violation: {}", ex.getMessage());
        return ApiResponse.fail(ResultCode.VALIDATION_ERROR.getCode(), ex.getMessage());
    }

    // 5. 处理 404 Not Found (防止 404 触发内部转发循环)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFoundException(NoHandlerFoundException ex) {
        logger.warn("404 Not Found: {}", ex.getRequestURL());
        return ApiResponse.fail(HttpStatus.NOT_FOUND.value(), "请求的资源不存在。");
    }

    // 6. 处理所有未被捕获的其它异常 (Exception.class)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 服务器内部错误返回 500
    public ApiResponse<String> handleException(Exception ex){
        logger.error("Unhandled Internal Server Error: {}", ex.getMessage(), ex);
        // 使用 ResultCode.SERVER_ERROR 提供的错误码
        return ApiResponse.fail(ResultCode.SERVER_ERROR);
    }
}
