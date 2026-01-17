package com.example.ai_kanban.security.model;

import com.example.ai_kanban.domain.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class LoginUser implements UserDetails {

    private final UserEntity user;
    private final List<String> roles;
    private final Collection<? extends GrantedAuthority> authorities;

    public LoginUser(UserEntity user, List<String> roles) {
        this.user = user;
        this.roles = (roles != null) ? roles : Collections.emptyList();
        assert roles != null;
        this.authorities = mapRoleToAuthorities(roles);
    }

    private Collection<? extends GrantedAuthority> mapRoleToAuthorities(List<String> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (user == null) return false;
        return user.getStatus() == 1;
    }
}
