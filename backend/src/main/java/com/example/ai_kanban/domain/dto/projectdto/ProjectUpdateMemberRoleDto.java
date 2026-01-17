package com.example.ai_kanban.domain.dto.projectdto;

import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectUpdateMemberRoleDto {
    @ProjectId
    private Long projectId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "角色不能为空")
    private Integer role;
}
