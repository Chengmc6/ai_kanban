package com.example.ai_kanban.domain.dto.projectdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectCreateDto {

    @NotBlank(message = "请输入项目名")
    private String projectName;
}
