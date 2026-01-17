package com.example.ai_kanban.domain.dto.projectdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AiBatchCreateProjectDto {

    @NotBlank(message = "项目名不能为空")
    private String projectName;
    // --- 统一的 AI 元数据 ---
    private Boolean isAiGenerated = true; // 默认 true
    private String aiPrompt;              // 用户原始提示词
    private String aiModelInfo;           // 模型名称（如 gemini-1.5-flash）

    @NotNull
    private List<ColumnItemDto> columns;

    @Data
    public static class ColumnItemDto{

        @NotBlank(message = "看板列名不能为空")
        private String columnName;
        @NotNull
        private List<TaskItemDto> tasks;
    }

    @Data
    public static class TaskItemDto{

        @NotBlank(message = "任务标题不能为空")
        private String title;
        private String content;
        private Byte priority;
        private LocalDateTime deadline;
    }
}
