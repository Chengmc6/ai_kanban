package com.example.ai_kanban.controller;

import com.example.ai_kanban.common.ApiResponse;
import com.example.ai_kanban.common.enums.ProjectRoleEnum;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.taskdto.*;
import com.example.ai_kanban.domain.service.TaskDetailService;
import com.example.ai_kanban.domain.service.TaskService;
import com.example.ai_kanban.permission.annotation.ProjectId;
import com.example.ai_kanban.permission.annotation.ProjectPermission;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 任务表 前端控制器
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Validated
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskDetailService detailService;

    /**
     * 创建任务
     * POST /task
     */
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    @PostMapping
    public ApiResponse<TaskBaseVo> createProjectTask(
            @RequestBody @Valid @NotNull TaskCreateDto dto) {

        return ApiResponse.success(taskService.createTask(dto));
    }

    /**
     * 查询任务详情
     * GET /task/{projectId}/{taskId}
     */
    @ProjectPermission(ProjectRoleEnum.MEMBER)
    @GetMapping("/{projectId}/{taskId}")
    public ApiResponse<TaskDetailsVo> getTaskDetails(
            @ProjectId @PathVariable @NotNull Long projectId,
            @PathVariable @NotNull Long taskId) {

        return ApiResponse.success(detailService.getTaskDetail(projectId, taskId));
    }

    /**
     * 删除任务
     * DELETE /task/{projectId}/{taskId}
     */
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    @DeleteMapping("/{projectId}/{taskId}")
    public ApiResponse<Void> removeProjectTask(
            @ProjectId @PathVariable @NotNull Long projectId,
            @PathVariable @NotNull Long taskId) {

        taskService.removeTask(projectId, taskId);
        return ApiResponse.success("删除成功");
    }

    /**
     * 更新任务基础信息
     * PUT /task/basic
     */
    @ProjectPermission(ProjectRoleEnum.MEMBER)
    @PutMapping("/basic")
    public ApiResponse<TaskDetailsVo> updateTaskBasic(
            @RequestBody @Valid @NotNull TaskUpdateBasicDto dto) {

        return ApiResponse.success(taskService.updateTaskBasic(dto));
    }

    /**
     * 更新任务优先级
     * PUT /task/priority
     */
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    @PutMapping("/priority")
    public ApiResponse<TaskDetailsVo> updateTaskPriority(
            @RequestBody @Valid @NotNull TaskUpdatePriorityDto dto) {

        return ApiResponse.success(taskService.updateTaskPriority(dto));
    }

    /**
     * 更新任务指派人
     * PUT /task/assignee
     */
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    @PutMapping("/assignee")
    public ApiResponse<TaskDetailsVo> updateTaskAssignee(
            @RequestBody @Valid @NotNull TaskUpdateAssigneeDto dto) {

        return ApiResponse.success(taskService.updateTaskAssignee(dto));
    }

    /**
     * 更新任务状态
     * PUT /task/status
     */
    @ProjectPermission(ProjectRoleEnum.MEMBER)
    @PutMapping("/status")
    public ApiResponse<TaskDetailsVo> updateTaskStatus(
            @RequestBody @Valid @NotNull TaskUpdateStatusDto dto) {

        return ApiResponse.success(taskService.updateTaskStatus(dto));
    }

    /**
     * 更新任务详情（前端）
     * PUT /task/detail
     */
    @ProjectPermission(ProjectRoleEnum.MEMBER)
    @PutMapping("/detail")
    public ApiResponse<TaskDetailsVo> updateTaskDetails(
            @RequestBody @NotNull @Valid TaskDetailsUpdateDto dto) {

        return ApiResponse.success(detailService.updateDetails(dto));
    }

    /**
     * 更新任务详情（AI）
     * PUT /task/detail/ai
     */
    @ProjectPermission(ProjectRoleEnum.MEMBER) // 或者 INTERNAL，取决于你怎么设计
    @PutMapping("/detail/ai")
    public ApiResponse<TaskDetailsVo> updateDetailsByAI(
            @RequestBody @NotNull @Valid AITaskDetailUpdateDto dto) {

        return ApiResponse.success(detailService.updateDetailsByAI(dto));
    }

    /**
     * 排序任务
     * PUT /task/sort
     */
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    @PutMapping("/sort")
    public ApiResponse<Void> sortTasks(
            @RequestBody @Valid @NotNull SortedTasksDto dto) {

        taskService.sortTasks(dto);
        return ApiResponse.success();
    }

    /**
     * 分页查询任务
     * POST /task/list
     */
    @ProjectPermission(ProjectRoleEnum.MEMBER)
    @PostMapping("/list")
    public ApiResponse<PageResult<TaskBaseVo>> queryTasks(
            @RequestBody @Valid @NotNull TaskQueryDto dto) {

        return ApiResponse.success(taskService.taskQuery(dto));
    }
}

