package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AITaskDetailUpdateDto {
    @NotNull(message = "请选择任务")
    private Long taskId;

    @ProjectId
    private Long projectId;

    private String content;
    private LocalDateTime deadline;

    private Boolean isAiGenerated;
    private String aiPrompt;
    private String aiModelInfo;
}
