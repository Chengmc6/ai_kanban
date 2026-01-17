package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskUpdatePriorityDto {
    @ProjectId
    private Long projectId;
    @NotNull(message = "请选择任务")
    private Long taskId;

    @NotNull
    private Long version;
    @NotNull
    private Byte priority;
}
