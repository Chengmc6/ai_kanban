package com.example.ai_kanban.security.handler;

import com.example.ai_kanban.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点异常处理器
 *
 * 该处理器实现了Spring Security的AuthenticationEntryPoint接口，
 * 用于处理未认证用户尝试访问受保护资源的请求。当请求缺少有效的JWT令牌或令牌已过期时，
 * 该处理器会返回统一的JSON格式错误响应，而非默认的错误页面。
 *
 * 处理的场景：
 * 1. 用户未登录就访问需要认证的资源
 * 2. JWT令牌已过期
 * 3. JWT令牌格式错误或无效
 * 4. 请求头中缺少Authorization令牌
 *
 * 响应标准：
 * - HTTP状态码：401 Unauthorized
 * - 响应格式：JSON
 * - 响应字符集：UTF-8
 * - 响应体：包含错误码和错误消息的ApiResponse对象
 *
 * @see org.springframework.security.web.AuthenticationEntryPoint
 * @see com.example.ai_kanban.common.ApiResponse
 * @author AI Kanban
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** JSON对象映射器，用于将ApiResponse对象序列化为JSON字符串 */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 处理认证异常
     *
     * 当用户请求受保护资源但未提供有效的认证信息时，此方法会被调用。
     * 该方法生成一个标准的JSON错误响应并返回给客户端。
     *
     * 处理流程：
     * 1. 设置响应内容类型为JSON格式
     * 2. 设置HTTP状态码为401（未授权）
     * 3. 构建包含错误信息的ApiResponse对象
     * 4. 将ApiResponse序列化为JSON字符串并写入响应流
     *
     * @param request HTTP请求对象，包含请求的所有信息
     * @param response HTTP响应对象，用于设置响应状态和内容
     * @param authException 认证异常对象，包含具体的认证失败原因
     * @throws IOException 如果向响应流写入数据时发生I/O错误
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 设置响应内容类型为JSON，使用UTF-8字符编码
        response.setContentType("application/json;charset=UTF-8");
        
        // 设置HTTP状态码为401（未授权/未认证）
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 构建失败的API响应对象
        ApiResponse<?> body = ApiResponse.fail(401, "未登录或登录已过期");
        
        // 将ApiResponse对象转换为JSON字符串并写入响应流
        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
