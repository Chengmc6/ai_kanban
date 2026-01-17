package com.example.ai_kanban.domain.dto.projectdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import lombok.Data;

@Data
public class ProjectUpdateDto {
    @ProjectId
    private Long projectId;
    private String name;
    private Long newUserId;
}
