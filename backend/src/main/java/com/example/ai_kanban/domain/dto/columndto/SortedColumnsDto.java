package com.example.ai_kanban.domain.dto.columndto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import com.example.ai_kanban.validation.annotation.NotEmptyElements;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SortedColumnsDto {
    @ProjectId
    private Long projectId;

    @NotEmptyElements
    private List<ColumnSortItem> columns;

    @Data
    @Valid
    public static class ColumnSortItem{
        @NotNull
        private Long columnId;
        @NotNull
        private Long version;
    }
}
