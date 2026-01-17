package com.example.ai_kanban.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ai_kanban.aop.annotation.ParameterNotNull;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.common.utils.UserFillHelper;
import com.example.ai_kanban.domain.dto.projectdto.AiBatchCreateProjectDto;
import com.example.ai_kanban.domain.dto.taskdto.*;
import com.example.ai_kanban.domain.entity.BoardColumnEntity;
import com.example.ai_kanban.domain.entity.ProjectEntity;
import com.example.ai_kanban.domain.entity.TaskDetailEntity;
import com.example.ai_kanban.domain.entity.TaskEntity;
import com.example.ai_kanban.domain.mapper.BoardColumnMapper;
import com.example.ai_kanban.domain.mapper.ProjectMapper;
import com.example.ai_kanban.domain.mapper.TaskMapper;
import com.example.ai_kanban.domain.mapstruct.TaskConvert;
import com.example.ai_kanban.domain.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskEntity> implements TaskService {

    private final TaskConvert taskConvert;
    private final UserService userService;
    private final ProjectMapper projectMapper;
    private final BoardColumnMapper columnMapper;
    private final TaskDetailService taskDetailService;

    @Override
    public List<TaskBaseVo> getTaskBaseByColumnIds(Long projectId, List<Long> columnIds) {

        if (columnIds == null || columnIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 查任务
        List<TaskEntity> tasks = this.list(
                new LambdaQueryWrapper<TaskEntity>()
                        .eq(TaskEntity::getProjectId, projectId)
                        .in(TaskEntity::getColumnId, columnIds)
                        .orderByDesc(TaskEntity::getOrderNum)
        );

        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 2.转换
        List<TaskBaseVo> taskBaseVos = tasks.stream()
                .map(taskConvert::toBaseVo)
                .toList();
        UserFillHelper.fillUserInfo(taskBaseVos, userService);
        return taskBaseVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAllTasks(List<Long> columnIds) {
        if (columnIds == null || columnIds.isEmpty()) return;
        this.remove(new LambdaQueryWrapper<TaskEntity>().in(TaskEntity::getColumnId, columnIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskBaseVo createTask(@ParameterNotNull TaskCreateDto dto) {

        ProjectEntity project = projectMapper.selectById(dto.getProjectId());

        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        LambdaQueryWrapper<BoardColumnEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BoardColumnEntity::getId, dto.getColumnId())
                .eq(BoardColumnEntity::getProjectId, dto.getProjectId())
                .getEntity();
        BoardColumnEntity column = columnMapper.selectOne(wrapper);
        if (column == null) {
            throw new SystemException(ResultCode.COLUMN_NOT_FOUND);
        }

        Integer maxOrderNum = this.lambdaQuery()
                .eq(TaskEntity::getColumnId, dto.getColumnId())
                .orderByDesc(TaskEntity::getOrderNum)
                .last("LIMIT 1")
                .oneOpt()
                .map(TaskEntity::getOrderNum)
                .orElse(0);

        Byte priority = dto.getPriority();
        if (priority == null) {
            priority = 1;
        }

        TaskEntity task = new TaskEntity();
        task.setColumnId(dto.getColumnId());
        task.setOrderNum(maxOrderNum + 1);
        task.setTitle(dto.getTitle());
        task.setPriority(priority);

        this.save(task);

        TaskDetailEntity taskDetail = new TaskDetailEntity();
        taskDetail.setTaskId(task.getId());

        taskDetailService.save(taskDetail);

        return taskConvert.toBaseVo(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTask(@ParameterNotNull Long projectId, @ParameterNotNull Long taskId) {

        boolean removed = this.lambdaUpdate().eq(TaskEntity::getId, taskId)
                .eq(TaskEntity::getProjectId, projectId)
                .remove();

        if (!removed) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDetailsVo updateTaskBasic(@ParameterNotNull TaskUpdateBasicDto dto) {

        // 1. 查询任务（校验项目所属权）
        TaskEntity task = this.getById(dto.getTaskId());
        if (task == null || !Objects.equals(task.getProjectId(), dto.getProjectId())) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

        // 2. 核心：无论修改标题还是内容，都利用版本号进行并发控制
        // 设置前端传回的原始版本号
        task.setVersion(dto.getVersion());
        boolean hasChanges = false;

        // 3. 处理标题更新
        if (dto.getTitle() != null && !Objects.equals(task.getTitle(), dto.getTitle())) {
            task.setTitle(dto.getTitle());
            hasChanges = true;
        }

        // 4. 执行更新并校验乐观锁
        if (hasChanges) {
            // 哪怕 Title 没改，这里执行 updateById 也会因为 @Version 机制导致 SQL：
            // UPDATE task SET version = version + 1 WHERE id = ? AND version = ?
            boolean updated = this.updateById(task);
            if (!updated) {
                throw new SystemException("任务已被他人修改，请刷新后重试", ResultCode.CONFLICT.getCode());
            }
        } else {
            // 如果 Title 是空的，直接返回当前详情即可
            return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
        }

        // 5. 重新查询最新数据（确保返回的是自增后的新 version 和最新内容）
        return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDetailsVo updateTaskPriority(@ParameterNotNull TaskUpdatePriorityDto dto) {

        TaskEntity task = this.getById(dto.getTaskId());
        if (task == null || !Objects.equals(task.getProjectId(), dto.getProjectId())) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

        if (Objects.equals(task.getPriority(), dto.getPriority())) {
            return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
        }

        task.setVersion(dto.getVersion());
        task.setPriority(dto.getPriority());

        boolean updated = this.updateById(task);

        if (!updated) {
            throw new SystemException("任务已被他人修改，请稍后再试", ResultCode.CONFLICT.getCode());
        }

        return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDetailsVo updateTaskAssignee(@ParameterNotNull TaskUpdateAssigneeDto dto) {

        TaskEntity task = this.getById(dto.getTaskId());
        if (task == null || !Objects.equals(task.getProjectId(), dto.getProjectId())) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

        if (Objects.equals(task.getAssigneeId(), dto.getAssigneeId())) {
            return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
        }

        task.setVersion(dto.getVersion());
        task.setAssigneeId(dto.getAssigneeId());

        boolean updated = this.updateById(task);

        if (!updated) {
            throw new SystemException("任务已被他人修改，请稍后再试", ResultCode.CONFLICT.getCode());
        }

        return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDetailsVo updateTaskStatus(@ParameterNotNull TaskUpdateStatusDto dto) {

        TaskEntity task = this.getById(dto.getTaskId());
        if (task == null || !Objects.equals(task.getProjectId(), dto.getProjectId())) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

        if (Objects.equals(task.getStatus(), dto.getStatus())) {
            return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
        }

        task.setStatus(dto.getStatus());
        task.setVersion(dto.getVersion());

        boolean updated = this.updateById(task);

        if (!updated) {
            throw new SystemException("任务已被他人修改，请稍后再试", ResultCode.CONFLICT.getCode());
        }

        return taskDetailService.getTaskDetail(dto.getProjectId(), dto.getTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortTasks(@ParameterNotNull SortedTasksDto dto) {

        ProjectEntity project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        LambdaQueryWrapper<BoardColumnEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BoardColumnEntity::getId, dto.getColumnId())
                .eq(BoardColumnEntity::getProjectId, dto.getProjectId())
                .getEntity();
        BoardColumnEntity column = columnMapper.selectOne(wrapper);
        if (column == null) {
            throw new SystemException(ResultCode.COLUMN_NOT_FOUND);
        }

        List<Long> ids = dto.getTaskItems().stream().map(SortedTasksDto.TaskSortItem::getTaskId).toList();

        List<TaskEntity> tasks = this.lambdaQuery()
                .eq(TaskEntity::getProjectId, dto.getProjectId())
                .eq(TaskEntity::getColumnId, dto.getColumnId())
                .in(TaskEntity::getId, ids)
                .list();

        if (tasks.size() != dto.getTaskItems().size()) {
            throw new SystemException("任务数据已变更，请刷新后重试");
        }

        Map<Long, TaskEntity> taskMap = tasks.stream()
                .collect(Collectors.toMap(TaskEntity::getId, t -> t));

        int sortNum = 1;
        for (var item : dto.getTaskItems()) {
            TaskEntity task = taskMap.get(item.getTaskId());
            task.setOrderNum(sortNum++);
            task.setVersion(item.getVersion());
        }

        boolean ok = this.updateBatchById(tasks);

        if (!ok) {
            throw new SystemException("排序冲突，请刷新后重试", ResultCode.CONFLICT.getCode());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<TaskBaseVo> taskQuery(@ParameterNotNull TaskQueryDto dto) {

        ProjectEntity project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        Page<TaskEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<TaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskEntity::getProjectId, dto.getProjectId())
                .like(StringUtils.hasText(dto.getKeywords()), TaskEntity::getTitle, dto.getKeywords())
                .orderByDesc(TaskEntity::getUpdateTime);

        Page<TaskEntity> usePage = this.page(page, wrapper);

        List<TaskBaseVo> vos = usePage.getRecords()
                .stream()
                .map(taskConvert::toBaseVo)
                .toList();

        if (vos.isEmpty()) {
            return PageResult.empty();
        }

        UserFillHelper.fillUserInfo(vos, userService);

        return new PageResult<>(
                usePage.getTotal(),
                usePage.getCurrent(),
                usePage.getSize(),
                usePage.getPages(),
                vos
        );
    }

    @Override
    public void createTasksFromAI(Long projectId,Long columnId, List<AiBatchCreateProjectDto.TaskItemDto> items, Boolean isAiGenerated, String aiPrompt, String aiModelInfo) {
        List<TaskEntity> tasks=new ArrayList<>();

        // 批量创建任务基本信息
        for (int i = 0; i < items.size(); i++) {
            var dto=items.get(i);
            TaskEntity task=new TaskEntity();
            task.setTitle(dto.getTitle());
            task.setPriority(dto.getPriority());
            task.setProjectId(projectId);
            task.setOrderNum(i+1);
            tasks.add(task);
        }

        // 批量插入任务
        this.saveBatch(tasks);

        // 批量创建任务详情
        List<TaskDetailEntity> details=new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            TaskEntity task=tasks.get(i);
            var dto=items.get(i);

            TaskDetailEntity detail=new TaskDetailEntity();
            detail.setTaskId(task.getId());
            detail.setContent(dto.getContent());
            detail.setDeadline(dto.getDeadline());
            detail.setIsAiGenerated(isAiGenerated);
            detail.setAiPrompt(aiPrompt);
            detail.setAiModelInfo(aiModelInfo);
            details.add(detail);
        }

        taskDetailService.saveBatch(details);
    }


}
