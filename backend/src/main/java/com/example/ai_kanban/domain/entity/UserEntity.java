package com.example.ai_kanban.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user")
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码（加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 状态：1启用 0禁用
     */
    @TableField("status")
    private Byte status;

    /**
     * 逻辑删除：0正常 1已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
