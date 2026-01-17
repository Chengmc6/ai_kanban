package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskUpdateBasicDto {
    @NotNull(message = "请选择任务")
    private Long taskId;
    @ProjectId
    private Long projectId;
    private String title;
    private Long version;
}
