package com.example.ai_kanban.domain.service;

import com.example.ai_kanban.domain.dto.taskdto.AITaskDetailUpdateDto;
import com.example.ai_kanban.domain.dto.taskdto.TaskDetailsUpdateDto;
import com.example.ai_kanban.domain.dto.taskdto.TaskDetailsVo;
import com.example.ai_kanban.domain.entity.TaskDetailEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 任务详情表 服务类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
public interface TaskDetailService extends IService<TaskDetailEntity> {
    TaskDetailsVo getTaskDetail(Long projectId, Long taskId);

    TaskDetailsVo updateDetails(TaskDetailsUpdateDto dto);

    TaskDetailsVo updateDetailsByAI(AITaskDetailUpdateDto dto);

}
