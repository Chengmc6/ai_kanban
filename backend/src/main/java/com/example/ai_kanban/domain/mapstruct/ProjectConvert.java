package com.example.ai_kanban.domain.mapstruct;

import com.example.ai_kanban.domain.dto.projectdto.ProjectBaseVo;
import com.example.ai_kanban.domain.dto.projectdto.ProjectDetailsVo;
import com.example.ai_kanban.domain.entity.ProjectEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectConvert {
    ProjectBaseVo toProjectBase(ProjectEntity project);
    ProjectDetailsVo toProjectDetail(ProjectEntity projectEntity);
}
