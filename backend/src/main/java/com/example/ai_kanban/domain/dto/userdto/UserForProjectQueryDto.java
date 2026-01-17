package com.example.ai_kanban.domain.dto.userdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import lombok.Data;

@Data
public class UserForProjectQueryDto {
    @ProjectId
    private Long projectId;
    private Integer pageNum=1;
    private Integer pageSize=10;
    private String keyword;
}
