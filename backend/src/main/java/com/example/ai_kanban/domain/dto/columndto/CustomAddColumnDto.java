package com.example.ai_kanban.domain.dto.columndto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomAddColumnDto {
    @ProjectId
    private Long projectId;
    @NotBlank(message = "请输入列名")
    private String name;
}
