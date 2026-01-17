package com.example.ai_kanban.domain.service.impl;

import com.example.ai_kanban.aop.annotation.ParameterNotNull;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.common.utils.UserFillHelper;
import com.example.ai_kanban.domain.dto.taskdto.AITaskDetailUpdateDto;
import com.example.ai_kanban.domain.dto.taskdto.TaskDetailsUpdateDto;
import com.example.ai_kanban.domain.dto.taskdto.TaskDetailsVo;
import com.example.ai_kanban.domain.entity.TaskDetailEntity;
import com.example.ai_kanban.domain.entity.TaskEntity;
import com.example.ai_kanban.domain.mapper.TaskDetailMapper;
import com.example.ai_kanban.domain.mapper.TaskMapper;
import com.example.ai_kanban.domain.mapstruct.TaskConvert;
import com.example.ai_kanban.domain.service.TaskDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_kanban.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Objects;

/**
 * <p>
 * 任务详情表 服务实现类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Service
@RequiredArgsConstructor
public class TaskDetailServiceImpl extends ServiceImpl<TaskDetailMapper, TaskDetailEntity> implements TaskDetailService {

    private final TaskMapper taskMapper;
    private final UserService userService;
    private final TaskConvert taskConvert;

    @Override
    @Transactional(readOnly = true)
    public TaskDetailsVo getTaskDetail(Long projectId, Long taskId) {

        TaskEntity task = taskMapper.selectById(taskId);
        if (task == null || !task.getProjectId().equals(projectId)) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

        TaskDetailEntity detail = this.getById(taskId);

        TaskDetailsVo vo = taskConvert.assemble(task, detail);

        UserFillHelper.fillUserInfo(Collections.singletonList(vo), userService);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDetailsVo updateDetails(@ParameterNotNull TaskDetailsUpdateDto dto) {
        TaskEntity task = taskMapper.selectById(dto.getTaskId());
        if (task == null || !Objects.equals(task.getProjectId(), dto.getProjectId())) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

        TaskDetailEntity detail = this.getById(dto.getTaskId());

        boolean isUpdated = false;

        if (StringUtils.hasText(dto.getContent()) && !Objects.equals(detail.getContent(), dto.getContent())) {
            detail.setContent(dto.getContent());
            isUpdated = true;
        }

        if (dto.getDeadline() != null && !Objects.equals(dto.getDeadline(), detail.getDeadline())) {
            detail.setDeadline(dto.getDeadline());
            isUpdated = true;
        }

        if (isUpdated) {
            boolean ok = this.updateById(detail);
            if (!ok) {
                throw new SystemException("任务已被他人修改，请刷新后重试", ResultCode.CONFLICT.getCode());
            }
        }

        return getTaskDetail(dto.getProjectId(), dto.getTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDetailsVo updateDetailsByAI(@ParameterNotNull AITaskDetailUpdateDto dto) {

        TaskEntity task = taskMapper.selectById(dto.getTaskId());
        if (task == null || !Objects.equals(task.getProjectId(), dto.getProjectId())) {
            throw new SystemException(ResultCode.TASK_NOT_FOUND);
        }

        TaskDetailEntity detail = this.getById(dto.getTaskId());

        boolean isUpdated = false;
        // 内容更新
        if (StringUtils.hasText(dto.getContent()) && !Objects.equals(detail.getContent(), dto.getContent())) {
            detail.setContent(dto.getContent());
            isUpdated = true;
        }
        // 截止时间更新
        if (dto.getDeadline() != null && !Objects.equals(dto.getDeadline(), detail.getDeadline())) {
            detail.setDeadline(dto.getDeadline());
            isUpdated = true;
        }
        // AI 字段（仅在非 null 时更新）
        if (dto.getIsAiGenerated() != null) {
            detail.setIsAiGenerated(dto.getIsAiGenerated());
            isUpdated = true;
        }
        if (dto.getAiPrompt() != null) {
            detail.setAiPrompt(dto.getAiPrompt());
            isUpdated = true;
        }
        if (dto.getAiModelInfo() != null) {
            detail.setAiModelInfo(dto.getAiModelInfo());
            isUpdated = true;
        }

        if (isUpdated) {
            boolean ok = this.updateById(detail);
            if (!ok) {
                throw new SystemException("任务已被他人修改，请刷新后重试", ResultCode.CONFLICT.getCode());
            }
        }

        return getTaskDetail(dto.getProjectId(), dto.getTaskId());
    }


}
