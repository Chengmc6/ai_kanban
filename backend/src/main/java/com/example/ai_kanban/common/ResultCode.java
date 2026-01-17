package com.example.ai_kanban.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SERVER_ERROR(500, "系统异常"),
    BAD_REQUEST(400, "错误请求"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    VALIDATION_ERROR(422,"参数校验失败"),
    USER_NOT_FOUND(1001, "用户不存在"),
    LOGIN_ERROR(1002, "用户名或密码错误，登录失败"),
    PASSWORD_REPETITION(1003,"新旧密码重复"),
    USERNAME_EXISTS(1004,"用户名已存在"),
    USER_DISABLED(1005,"用户已被禁用"),
    USER_DELETED(1006,"用户已被删除"),
    PASSWORD_INCORRECT(1007,"密码错误"),
    CAN_NOT_REMOVE_SELF(1008,"不能删除当前登录用户"),
    CAN_NOT_DISABLE_SELF(1009,"不能禁用当前登录用户"),
    TOKEN_EXPIRED(2001, "Token 已过期"),
    DATA_ALREADY_UPDATED(2002,"操作已被处理"),
    PROJECT_NOT_FOUND(3001,"项目不存在"),
    CAN_NOT_REMOVE_MEMBER(3002,"不能移除改项目成员"),
    MEMBER_NOT_FOUND(3002,"成员不存在"),
    CAN_NOT_MODIFY_OWNER(3003,"不能修改项目拥有者"),
    MEMBER_ALREADY_EXISTS(3004,"成员已存在项目中"),
    CONFLICT(3005,"数据冲突"),
    COLUMN_NOT_FOUND(3006,"列不存在"),
    TASK_NOT_FOUND(4001,"任务不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
