package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCreateDto {
    @ProjectId
    private Long projectId;
    @NotNull(message = "请选择看板列")
    private Long columnId;
    @NotBlank(message = "请输入任务标题")
    private String title;

    private Byte priority;
}
