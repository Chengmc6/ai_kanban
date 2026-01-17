package com.example.ai_kanban.security.jwt;

import com.example.ai_kanban.security.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "ai_kanban-jwt-secret-key-2026-test";
    private static final long EXPIRATION = 86400000;
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(LoginUser userDetails){
        return Jwts.builder()
                .setSubject(String.valueOf(userDetails.getUserId()))
                .claim("username", userDetails.getUsername())
                .claim("roles", userDetails.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getToken(Long userId, String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractToken(String authorizationHeader){
        if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public Long getUserId(String token){
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String getUsername(String token){
        return getClaims(token).get("username",String.class);
    }

    public List<String> getRoles(String token){
        return getClaims(token).get("roles",List.class);
    }

    public boolean isTokenExpired(String token){
        return getClaims(token).getExpiration().before(new Date());
    }
}
