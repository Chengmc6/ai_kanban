package com.example.ai_kanban.permission.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.enums.ProjectRoleEnum;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.domain.entity.ProjectMemberEntity;
import com.example.ai_kanban.domain.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PermissionChecker {

    private final ProjectMemberService memberService;

    /**
     * 校验用户是否有权限操作项目
     */
    public void checkProjectPermission(Long projectId, Long userId, ProjectRoleEnum requiredRole){

        ProjectMemberEntity member=memberService.getOne(new LambdaQueryWrapper<ProjectMemberEntity>()
                .eq(ProjectMemberEntity::getProjectId,projectId)
                .eq(ProjectMemberEntity::getUserId,userId),false
        );

        if (member == null) {
            throw new SystemException("你不是项目成员，无权限访问", ResultCode.FORBIDDEN.getCode());
        }

        ProjectRoleEnum userRole=ProjectRoleEnum.fromCode(member.getRole());

        // 校验角色
        if (userRole==null || !userRole.hasPermission(requiredRole)) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }

    }

}
