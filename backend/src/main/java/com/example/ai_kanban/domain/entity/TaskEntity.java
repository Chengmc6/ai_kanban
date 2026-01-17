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
 * 任务表
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("task")
public class TaskEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属列ID
     */
    @TableField("column_id")
    private Long columnId;

    /**
     * 任务标题
     */
    @TableField("title")
    private String title;

    /**
     * 优先级：1普通 2重要 3紧急
     */
    @TableField("priority")
    private Byte priority;

    /**
     * 指派给的用户ID
     */
    @TableField("assignee_id")
    private Long assigneeId;

    /**
     * 排序序号
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 状态：0未开始 1进行中 2已完成
     */
    @TableField("status")
    private Byte status;//目前不使用

    /**
     * 逻辑删除
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

    /**
     * 所属项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 版本号
     */
    @Version
    @TableField("version")
    private Long version;
}
