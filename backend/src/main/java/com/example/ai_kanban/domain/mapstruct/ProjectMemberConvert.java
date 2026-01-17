package com.example.ai_kanban.domain.mapstruct;

import com.example.ai_kanban.domain.dto.projectdto.ProjectMemberDetailVo;
import com.example.ai_kanban.domain.entity.ProjectMemberEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMemberConvert {
    ProjectMemberDetailVo toDetail(ProjectMemberEntity projectMemberEntity);
}
