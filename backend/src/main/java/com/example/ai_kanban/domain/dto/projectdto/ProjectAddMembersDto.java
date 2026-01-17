package com.example.ai_kanban.domain.dto.projectdto;

import com.example.ai_kanban.validation.annotation.NotEmptyElements;
import com.example.ai_kanban.permission.annotation.ProjectId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProjectAddMembersDto {
    @ProjectId
    private Long projectId;

    @NotEmptyElements(message = "请选择用户")
    @Valid
    private List<MemberItem> members;

    @Data
    public static class MemberItem{
        @NotNull(message = "用户ID不能为空")
        private Long userId;
        private Integer role;
    }
}
