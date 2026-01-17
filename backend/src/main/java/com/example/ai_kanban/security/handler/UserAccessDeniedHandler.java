package com.example.ai_kanban.security.handler;

import com.example.ai_kanban.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用户访问拒绝异常处理器
 *
 * 该处理器实现了Spring Security的AccessDeniedHandler接口，
 * 用于处理已认证用户但缺少访问特定资源所需权限的请求。
 * 当用户试图访问其权限范围外的资源时，该处理器会返回统一的JSON格式错误响应。
 *
 * 处理的场景：
 * 1. 用户已登录但权限不足
 * 2. 缺少执行操作所需的特定角色
 * 3. 缺少执行操作所需的特定权限
 * 4. 访问被限制的功能或数据资源
 *
 * 与JwtAuthenticationEntryPoint的区别：
 * - JwtAuthenticationEntryPoint：处理未认证情况（没有有效令牌）
 * - UserAccessDeniedHandler：处理已认证但权限不足的情况
 *
 * 响应标准：
 * - HTTP状态码：403 Forbidden
 * - 响应格式：JSON
 * - 响应字符集：UTF-8
 * - 响应体：包含错误码和错误消息的ApiResponse对象
 *
 * @see org.springframework.security.web.access.AccessDeniedHandler
 * @see com.example.ai_kanban.common.ApiResponse
 * @author AI Kanban
 */
@Component
public class UserAccessDeniedHandler implements AccessDeniedHandler {

    /** JSON对象映射器，用于将ApiResponse对象序列化为JSON字符串 */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 处理访问拒绝异常
     *
     * 当用户已经过认证但缺少访问受保护资源所需的权限时，此方法会被调用。
     * 该方法生成一个标准的JSON错误响应并返回给客户端。
     *
     * 处理流程：
     * 1. 设置响应内容类型为JSON格式
     * 2. 设置HTTP状态码为403（禁止）
     * 3. 构建包含错误信息的ApiResponse对象
     * 4. 将ApiResponse序列化为JSON字符串并写入响应流
     *
     * @param request HTTP请求对象，包含请求的所有信息
     * @param response HTTP响应对象，用于设置响应状态和内容
     * @param accessDeniedException 访问拒绝异常对象，包含具体的权限拒绝原因
     * @throws IOException 如果向响应流写入数据时发生I/O错误
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // 设置响应内容类型为JSON，使用UTF-8字符编码
        response.setContentType("application/json;charset=UTF-8");
        
        // 设置HTTP状态码为403（禁止/权限不足）
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 构建失败的API响应对象
        ApiResponse<?> body = ApiResponse.fail(403, "无权限访问");
        
        // 将ApiResponse对象转换为JSON字符串并写入响应流
        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
