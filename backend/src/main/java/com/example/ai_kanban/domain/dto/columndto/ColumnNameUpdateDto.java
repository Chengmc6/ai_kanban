package com.example.ai_kanban.domain.dto.columndto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ColumnNameUpdateDto {
    @ProjectId
    private Long projectId;

    @NotNull(message = "请选择需要修改的列")
    private Long id;

    @NotBlank(message = "请输入列名")
    private String name;

    @NotNull
    private Long version;
}
