package com.example.ai_kanban.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目成员表
 * </p>
 *
 * @author 高明
 * @since 2025-12-27
 */
@Data
@Accessors(chain = true)
@TableName("project_member")
public class ProjectMemberEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 成员用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 项目内角色：1成员 2管理员 3拥有者
     */
    @TableField("role")
    private Integer role;

    /**
     * 加入时间
     */
    @TableField(value = "join_time",fill = FieldFill.INSERT)
    private LocalDateTime joinTime;

    /**
     * 逻辑删除：0正常 1已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}
