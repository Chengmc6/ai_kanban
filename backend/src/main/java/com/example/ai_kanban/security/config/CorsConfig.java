package com.example.ai_kanban.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许 Vue 前端地址
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // 允许所有方法，包括 OPTIONS, POST, GET, etc.
        configuration.setAllowedMethods(Collections.singletonList("*"));
        // 允许所有 Header，以便携带 Content-Type, Authorization 等
        configuration.setAllowedHeaders(List.of("*"));
        // 允许携带认证信息（JWT, Cookie等）
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 对所有路径生效
        return source;
    }
}
