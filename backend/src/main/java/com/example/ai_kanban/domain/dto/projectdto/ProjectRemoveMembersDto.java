package com.example.ai_kanban.domain.dto.projectdto;

import com.example.ai_kanban.validation.annotation.NotEmptyElements;
import com.example.ai_kanban.permission.annotation.ProjectId;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRemoveMembersDto {
    @ProjectId
    private Long projectId;

    @NotEmptyElements(message = "请选择成员")
    private List<Long> userIds;
}
