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
 * 任务详情表
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("task_detail")
public class TaskDetailEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId("task_id")
    private Long taskId;

    /**
     * 任务内容
     */
    @TableField("content")
    private String content;

    /**
     * 截止时间
     */
    @TableField("deadline")
    private LocalDateTime deadline;

    /**
     * 是否AI生成
     */
    @TableField("is_ai_generated")
    private Boolean isAiGenerated;

    /**
     * AI提示词
     */
    @TableField("ai_prompt")
    private String aiPrompt;

    /**
     * AI模型信息
     */
    @TableField("ai_model_info")
    private String aiModelInfo;

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
