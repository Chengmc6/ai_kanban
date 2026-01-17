package com.example.ai_kanban.domain.service;

import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.projectdto.*;
import com.example.ai_kanban.domain.entity.ProjectEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目表 服务类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
public interface ProjectService extends IService<ProjectEntity> {

    ProjectEntity executeCreate(Long ownerId, String projectName);

    ProjectBaseVo createProject(Long ownerId, ProjectCreateDto dto);

    ProjectBaseVo batchCreateProject(Long ownerId, AiBatchCreateProjectDto dto);

    ProjectDetailsVo getProjectDetail(Long projectId);

    ProjectDetailsVo updateForProject(ProjectUpdateDto dto);

    void removeProject(Long projectId);

    PageResult<ProjectBaseVo> projectQuery(Long currentUserId, ProjectQueryDto dto);
}
