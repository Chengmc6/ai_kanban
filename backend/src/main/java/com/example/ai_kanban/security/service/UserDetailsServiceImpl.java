package com.example.ai_kanban.security.service;

import com.example.ai_kanban.domain.entity.UserEntity;
import com.example.ai_kanban.domain.mapper.UserMapper;
import com.example.ai_kanban.security.model.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user=userMapper.selectByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException(username+"Not Found");
        }
        // 额外的安全检查：确保密码哈希值不为空
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UsernameNotFoundException("用户 [" + username + "] 密码信息缺失。");
        }
        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        return new LoginUser(user, roles);
    }
}
