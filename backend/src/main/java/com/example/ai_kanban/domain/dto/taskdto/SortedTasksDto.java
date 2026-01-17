package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import com.example.ai_kanban.validation.annotation.NotEmptyElements;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SortedTasksDto {
    @ProjectId
    private Long projectId;

    @NotNull
    private Long columnId;

    @NotEmptyElements
    private List<TaskSortItem> taskItems;

    @Data
    @Valid
    public static class TaskSortItem{
        @NotNull
        private Long taskId;
        @NotNull
        private Long version;
    }
}
