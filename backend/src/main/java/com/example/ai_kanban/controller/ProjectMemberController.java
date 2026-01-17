package com.example.ai_kanban.controller;

import com.example.ai_kanban.common.ApiResponse;
import com.example.ai_kanban.common.enums.ProjectRoleEnum;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.projectdto.ProjectAddMembersDto;
import com.example.ai_kanban.domain.dto.projectdto.ProjectMemberDetailVo;
import com.example.ai_kanban.domain.dto.projectdto.ProjectRemoveMembersDto;
import com.example.ai_kanban.domain.dto.projectdto.ProjectUpdateMemberRoleDto;
import com.example.ai_kanban.domain.dto.userdto.UserForProjectQueryDto;
import com.example.ai_kanban.domain.dto.userdto.UserSimpleVo;
import com.example.ai_kanban.domain.service.ProjectMemberService;
import com.example.ai_kanban.permission.annotation.ProjectPermission;
import com.example.ai_kanban.security.model.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目成员表 前端控制器
 * </p>
 *
 * @author 高明
 * @since 2025-12-27
 */
@Validated
@RestController
@RequestMapping("/project-member")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService memberService;

    /**
     * 查询可加入项目的用户（分页）
     * GET /project-member/available
     */
    @ProjectPermission(ProjectRoleEnum.MEMBER)
    @GetMapping("/available")
    public ApiResponse<PageResult<UserSimpleVo>> getProjectMember(
            @Valid @NotNull UserForProjectQueryDto dto) {

        return ApiResponse.success(memberService.getAvailableUsersForProject(dto));
    }

    /**
     * 添加项目成员
     * POST /project-member
     */
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    @PostMapping
    public ApiResponse<List<ProjectMemberDetailVo>> addProjectMembers(
            @Valid @NotNull @RequestBody ProjectAddMembersDto dto) {

        return ApiResponse.success(memberService.addProjectMembers(dto));
    }

    /**
     * 移除项目成员
     * DELETE /project-member
     */
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    @DeleteMapping
    public ApiResponse<Void> removeProjectMembers(
            @Valid @NotNull @RequestBody ProjectRemoveMembersDto dto,
            @AuthenticationPrincipal LoginUser loginUser) {

        memberService.removeProjectMembers(dto, loginUser.getUserId());
        return ApiResponse.success("删除成功");
    }

    /**
     * 更新成员角色
     * PUT /project-member/role
     */
    @ProjectPermission(ProjectRoleEnum.OWNER)
    @PutMapping("/role")
    public ApiResponse<ProjectMemberDetailVo> updateProjectMemberRole(
            @Valid @NotNull @RequestBody ProjectUpdateMemberRoleDto dto) {

        return ApiResponse.success(memberService.updateMemberRole(dto));
    }
}

