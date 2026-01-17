package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDetailsUpdateDto {
    @NotNull(message = "请选择任务")
    private Long taskId;

    @ProjectId
    private Long projectId;

    private String content;

    private LocalDateTime deadline;
}
