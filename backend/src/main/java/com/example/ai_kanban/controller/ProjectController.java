package com.example.ai_kanban.controller;

import com.example.ai_kanban.common.ApiResponse;
import com.example.ai_kanban.common.enums.ProjectRoleEnum;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.projectdto.*;
import com.example.ai_kanban.domain.service.ProjectService;
import com.example.ai_kanban.permission.annotation.ProjectId;
import com.example.ai_kanban.permission.annotation.ProjectPermission;
import com.example.ai_kanban.security.model.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 项目表 前端控制器
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Validated
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 创建项目
     * POST /project
     */
    @PostMapping
    public ApiResponse<ProjectBaseVo> createProject(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody @Valid @NotNull ProjectCreateDto dto) {

        return ApiResponse.success(projectService.createProject(loginUser.getUserId(), dto));
    }

    /**
     * AI批量创建项目
     * POST /project/ai/batch
     */
    @PostMapping("/ai/batch")
    public ApiResponse<ProjectBaseVo> aiBatchCreateProject(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody @Valid @NotNull AiBatchCreateProjectDto dto){
        return ApiResponse.success(projectService.batchCreateProject(loginUser.getUserId(), dto));
    }

    /**
     * 获取项目详情
     * GET /project/{projectId}
     */
    @GetMapping("/{projectId}")
    @ProjectPermission(ProjectRoleEnum.MEMBER)
    public ApiResponse<ProjectDetailsVo> getProjectDetails(
            @PathVariable @ProjectId Long projectId) {

        return ApiResponse.success(projectService.getProjectDetail(projectId));
    }

    /**
     * 更新项目
     * PUT /project
     */
    @PutMapping
    @ProjectPermission(ProjectRoleEnum.OWNER)
    public ApiResponse<ProjectDetailsVo> updateProject(
            @RequestBody @Valid @NotNull ProjectUpdateDto dto) {

        return ApiResponse.success(projectService.updateForProject(dto));
    }

    /**
     * 删除项目
     * DELETE /project/{projectId}
     */
    @DeleteMapping("/{projectId}")
    @ProjectPermission(ProjectRoleEnum.OWNER)
    public ApiResponse<Void> removeProject(
            @PathVariable @ProjectId Long projectId) {

        projectService.removeProject(projectId);
        return ApiResponse.success("删除成功");
    }

    /**
     * 分页查询项目列表
     * POST /project/list
     */
    @PostMapping("/list")
    public ApiResponse<PageResult<ProjectBaseVo>> queryForProject(
            @RequestBody @Valid @NotNull ProjectQueryDto dto,
            @AuthenticationPrincipal LoginUser loginUser) {

        return ApiResponse.success(projectService.projectQuery(loginUser.getUserId(), dto));
    }
}

