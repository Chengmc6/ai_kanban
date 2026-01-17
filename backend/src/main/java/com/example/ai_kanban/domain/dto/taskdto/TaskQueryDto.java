package com.example.ai_kanban.domain.dto.taskdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import lombok.Data;

@Data
public class TaskQueryDto {
    @ProjectId
    private Long projectId;
    private Integer pageNum=1;
    private Integer pageSize=10;
    private String keywords;

}
