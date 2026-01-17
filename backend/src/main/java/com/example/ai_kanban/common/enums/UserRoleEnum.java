package com.example.ai_kanban.common.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ADMIN(1L, "ROLE_ADMIN", "系统管理员"),
    USER(2L, "ROLE_USER", "普通用户");

    private final Long id;
    private final String code; // 对应 Spring Security 中的角色代码
    private final String description;

    UserRoleEnum(Long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    /**
     * 根据 ID 获取枚举（可选，用于某些转换逻辑）
     */
    public static UserRoleEnum getRoleById(Long id) {
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        return null;
    }
}
