package com.example.ai_kanban.domain.service;

import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.projectdto.AiBatchCreateProjectDto;
import com.example.ai_kanban.domain.dto.taskdto.*;
import com.example.ai_kanban.domain.entity.TaskEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 任务表 服务类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
public interface TaskService extends IService<TaskEntity> {

    List<TaskBaseVo> getTaskBaseByColumnIds(Long projectId, List<Long> columnIds);

    void removeAllTasks(List<Long> columnIds);

    TaskBaseVo createTask(TaskCreateDto dto);

    void removeTask(Long projectId, Long taskId);

    TaskDetailsVo updateTaskBasic(TaskUpdateBasicDto dto);

    TaskDetailsVo updateTaskPriority(TaskUpdatePriorityDto dto);

    TaskDetailsVo updateTaskAssignee(TaskUpdateAssigneeDto dto);

    TaskDetailsVo updateTaskStatus(TaskUpdateStatusDto dto);

    void sortTasks(SortedTasksDto dto);

    PageResult<TaskBaseVo> taskQuery(TaskQueryDto dto);

    void createTasksFromAI(Long projectId,
                           Long columnId,
                           List<AiBatchCreateProjectDto.TaskItemDto> items,
                           Boolean isAiGenerated,
                           String aiPrompt,
                           String aiModelInfo);
}
