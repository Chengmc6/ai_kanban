package com.example.ai_kanban.domain.service;

import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.domain.dto.userdto.UserLoginVO;
import com.example.ai_kanban.security.jwt.JwtUtil;
import com.example.ai_kanban.security.model.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserLoginVO authenticateUser(String username, String password) {
        //认证
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        LoginUser userDetails = (LoginUser) authentication.getPrincipal();

        if (userDetails.getUser().getIsDeleted()) {
            throw new SystemException(ResultCode.USER_DELETED);
        }

        //生成token
        String token = jwtUtil.generateToken(userDetails);
        //封装返回DTO
        return new UserLoginVO(userDetails.getUsername(), token);
    }
}
