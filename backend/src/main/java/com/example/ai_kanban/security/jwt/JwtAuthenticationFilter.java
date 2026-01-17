package com.example.ai_kanban.security.jwt;

import com.example.ai_kanban.domain.entity.UserEntity;
import com.example.ai_kanban.domain.mapper.UserMapper;
import com.example.ai_kanban.security.model.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.extractToken(request.getHeader("Authorization"));
        if (token != null && !jwtUtil.isTokenExpired(token)) {
            Long userId=jwtUtil.getUserId(token);
            List<String> roles=jwtUtil.getRoles(token);

            UserEntity user=userMapper.selectById(userId);
            if(user!=null){
                LoginUser userDetails=new LoginUser(user,roles);

                UsernamePasswordAuthenticationToken authenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
