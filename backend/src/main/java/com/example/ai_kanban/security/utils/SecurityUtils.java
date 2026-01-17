package com.example.ai_kanban.security.utils;

import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.security.model.LoginUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全服务工具类
 */
public class SecurityUtils {

    /**
     * 获取当前的认证信息
     * 内部使用，处理未登录的异常抛出
     */
    private static LoginUser getAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 检查是否认证，且排除匿名用户 (AnonymousAuthenticationToken)
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new SystemException(ResultCode.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof LoginUser)) {
            throw new SystemException(ResultCode.UNAUTHORIZED);
        }

        return (LoginUser) principal;
    }

    /**
     * 获取当前登录用户ID
     * 适用于必须登录的业务场景，未登录会直接抛出 SystemException
     */
    public static Long getCurrentUserId() {
        return getAuth().getUserId();
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        return getAuth().getUsername();
    }

    /**
     * 获取完整的登录用户信息
     */
    public static LoginUser getLoginUser() {
        return getAuth();
    }

    /**
     * 【可选】静默获取用户ID
     * 适用于：某些接口登录与不登录表现不一致，但不会报错的场景
     * @return 用户ID，若未登录则返回 null
     */
    public static Long getUserIdQuietly() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof LoginUser) {
                return ((LoginUser) auth.getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}