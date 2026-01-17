package com.example.ai_kanban.common.enums;

import lombok.Getter;

@Getter
public enum ProjectRoleEnum {

    MEMBER(1, "成员"),
    ADMIN(2, "管理员"),
    OWNER(3, "拥有者");

    private final int code;
    private final String description;

    ProjectRoleEnum(int code, String description) {
        this.code = code; this.description = description;
    }

    public static ProjectRoleEnum fromCode(Integer code) {
        if (code == null) return null;
        for (ProjectRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }

    /** 判断是否是管理员（含拥有者） */
    public boolean isAdminOrAbove() {
        return this.code >= ADMIN.code;
    }

    /** 判断是否是拥有者 */
    public boolean isOwner() {
        return this == OWNER;
    }

    /** 判断是否是拥有权限 */
    public boolean hasPermission(ProjectRoleEnum requiredRole) {
        if (requiredRole == null) return false;
        return this.code >= requiredRole.code;
    }

}
