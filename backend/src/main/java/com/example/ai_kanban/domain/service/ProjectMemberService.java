package com.example.ai_kanban.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.projectdto.ProjectAddMembersDto;
import com.example.ai_kanban.domain.dto.projectdto.ProjectMemberDetailVo;
import com.example.ai_kanban.domain.dto.projectdto.ProjectRemoveMembersDto;
import com.example.ai_kanban.domain.dto.projectdto.ProjectUpdateMemberRoleDto;
import com.example.ai_kanban.domain.dto.userdto.UserForProjectQueryDto;
import com.example.ai_kanban.domain.dto.userdto.UserSimpleVo;
import com.example.ai_kanban.domain.entity.ProjectMemberEntity;

import java.util.List;

/**
 * <p>
 * 项目成员表 服务类
 * </p>
 *
 * @author 高明
 * @since 2025-12-27
 */
public interface ProjectMemberService extends IService<ProjectMemberEntity> {
    List<ProjectMemberDetailVo> getMembersVo(Long projectId);

    void updateOwner(Long projectId, Long oldUserId, Long newUserId);

    void insertOwnerMember(Long projectId, Long userId);

    void removeProjectAllMembers(Long projectId);

    PageResult<UserSimpleVo> getAvailableUsersForProject(UserForProjectQueryDto dto);

    List<ProjectMemberDetailVo> addProjectMembers(ProjectAddMembersDto dto);

    void removeProjectMembers(ProjectRemoveMembersDto dto, Long currentUserId);

    ProjectMemberDetailVo updateMemberRole(ProjectUpdateMemberRoleDto dto);
}
