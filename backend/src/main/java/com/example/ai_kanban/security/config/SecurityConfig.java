package com.example.ai_kanban.security.config;

import com.example.ai_kanban.security.handler.JwtAuthenticationEntryPoint;
import com.example.ai_kanban.security.handler.UserAccessDeniedHandler;
import com.example.ai_kanban.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            UserAccessDeniedHandler userAccessDeniedHandler
    ) throws Exception {
        return http
                // 1. 关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 🌟 显式启用 CORS 配置 (将使用 corsConfigurationSource Bean)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // 3. 配置 Session 为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. 配置认证规则
                .authorizeHttpRequests(auth -> auth
                        // 🌟 解决 CORS 核心问题：允许所有 OPTIONS 请求通过
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 允许注册和登录
                        .requestMatchers("/user/login", "/user").permitAll()

                        .anyRequest().authenticated()
                )
                // 🌟 5. 显式配置异常处理：确保认证失败时返回 401，防止重定向循环
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401
                        .accessDeniedHandler(userAccessDeniedHandler) // 403
                )
                // 6. 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
