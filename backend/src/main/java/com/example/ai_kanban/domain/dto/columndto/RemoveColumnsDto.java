package com.example.ai_kanban.domain.dto.columndto;

import com.example.ai_kanban.validation.annotation.NotEmptyElements;
import com.example.ai_kanban.permission.annotation.ProjectId;
import lombok.Data;

import java.util.List;

@Data
public class RemoveColumnsDto {
    @ProjectId
    private Long projectId;

    @NotEmptyElements(message = "请选择需要删除的列")
    private List<Long> columnIds;
}
