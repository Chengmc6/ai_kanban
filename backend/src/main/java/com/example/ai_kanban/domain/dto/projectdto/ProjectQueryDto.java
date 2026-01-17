package com.example.ai_kanban.domain.dto.projectdto;

import lombok.Data;

@Data
public class ProjectQueryDto {
    private Integer pageNum=1;
    private Integer pageSize=10;
    private String keyword;
}
