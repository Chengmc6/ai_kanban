package com.example.ai_kanban.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_kanban.aop.annotation.ParameterNotNull;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.enums.ProjectRoleEnum;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.common.utils.UserFillHelper;
import com.example.ai_kanban.domain.dto.projectdto.ProjectAddMembersDto;
import com.example.ai_kanban.domain.dto.projectdto.ProjectMemberDetailVo;
import com.example.ai_kanban.domain.dto.projectdto.ProjectRemoveMembersDto;
import com.example.ai_kanban.domain.dto.projectdto.ProjectUpdateMemberRoleDto;
import com.example.ai_kanban.domain.dto.userdto.UserForProjectQueryDto;
import com.example.ai_kanban.domain.dto.userdto.UserSimpleVo;
import com.example.ai_kanban.domain.entity.ProjectEntity;
import com.example.ai_kanban.domain.entity.ProjectMemberEntity;
import com.example.ai_kanban.domain.mapper.ProjectMapper;
import com.example.ai_kanban.domain.mapper.ProjectMemberMapper;
import com.example.ai_kanban.domain.mapstruct.ProjectMemberConvert;
import com.example.ai_kanban.domain.service.ProjectMemberService;
import com.example.ai_kanban.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目成员表 服务实现类
 * </p>
 *
 * @author 高明
 * @since 2025-12-27
 */
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper, ProjectMemberEntity> implements ProjectMemberService {

    private final ProjectMemberConvert memberConvert;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectMemberDetailVo> getMembersVo(Long projectId) {

        if (projectId == null) {
            throw new SystemException("项目id不能为空", ResultCode.BAD_REQUEST.getCode());
        }

        //查询成员
        List<ProjectMemberEntity> members = this.list(new LambdaQueryWrapper<ProjectMemberEntity>()
                .eq(ProjectMemberEntity::getProjectId, projectId));

        if (members.isEmpty()) {
            return Collections.emptyList();
        }

        //转换成vo
        List<ProjectMemberDetailVo> memberDetailVos = members.stream()
                .map(memberConvert::toDetail)
                .collect(Collectors.toList());
        UserFillHelper.fillUserInfo(memberDetailVos, userService);

        return memberDetailVos;
    }

    @Override
    public void updateOwner(Long projectId, Long oldUserId, Long newUserId) {

        if (Objects.equals(oldUserId, newUserId)) return;

        // 1. 将旧 Owner 降级为普通成员
        this.update(new LambdaUpdateWrapper<ProjectMemberEntity>()
                .set(ProjectMemberEntity::getRole, ProjectRoleEnum.MEMBER.getCode())
                .eq(ProjectMemberEntity::getProjectId, projectId)
                .eq(ProjectMemberEntity::getUserId, oldUserId));

        // 2. 处理新 Owner
        ProjectMemberEntity newMember = this.getOne(new LambdaQueryWrapper<ProjectMemberEntity>()
                .eq(ProjectMemberEntity::getProjectId, projectId)
                .eq(ProjectMemberEntity::getUserId, newUserId), false
        );

        if (newMember == null) {
            insertOwnerMember(projectId, newUserId);
        } else {
            newMember.setRole(ProjectRoleEnum.OWNER.getCode());
            this.updateById(newMember);
        }
    }

    @Override
    public void insertOwnerMember(Long projectId, Long userId) {

        if (projectId == null || userId == null) {
            throw new SystemException("缺少必要字段", ResultCode.BAD_REQUEST.getCode());
        }

        if(projectMapper.selectById(projectId)==null){
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        ProjectMemberEntity member = new ProjectMemberEntity();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(ProjectRoleEnum.OWNER.getCode());
        this.save(member);
    }

    @Override
    public void removeProjectAllMembers(Long projectId) {

        this.remove(new LambdaQueryWrapper<ProjectMemberEntity>().eq(ProjectMemberEntity::getProjectId, projectId));
    }

    @Override
    public PageResult<UserSimpleVo> getAvailableUsersForProject(@ParameterNotNull UserForProjectQueryDto dto) {

        if (projectMapper.selectById(dto.getProjectId()) == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        List<ProjectMemberEntity> members = this.list(new LambdaQueryWrapper<ProjectMemberEntity>()
                .eq(ProjectMemberEntity::getProjectId, dto.getProjectId()));

        List<Long> memberIds = members.stream().map(ProjectMemberEntity::getUserId).toList();

        return userService.getUsersForProject(dto, memberIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ProjectMemberDetailVo> addProjectMembers(@ParameterNotNull ProjectAddMembersDto dto) {

        Long projectId = dto.getProjectId();

        if (projectMapper.selectById(projectId) == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        // 1️⃣ 提取 userId
        List<Long> userIds = dto.getMembers().stream()
                .map(ProjectAddMembersDto.MemberItem::getUserId)
                .distinct()
                .toList();

        // 2️⃣ 查已存在
        List<Long> existedUserIds = this.lambdaQuery()
                .eq(ProjectMemberEntity::getProjectId, projectId)
                .in(ProjectMemberEntity::getUserId, userIds)
                .list()
                .stream()
                .map(ProjectMemberEntity::getUserId)
                .toList();

        // 3️⃣ 只插真正新增的
        List<ProjectMemberEntity> toInsert = dto.getMembers().stream()
                .filter(m -> !existedUserIds.contains(m.getUserId()))
                .map(m -> {
                    ProjectMemberEntity e = new ProjectMemberEntity();
                    e.setProjectId(projectId);
                    e.setUserId(m.getUserId());
                    e.setRole(m.getRole());
                    return e;
                }).toList();

        if (toInsert.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            this.saveBatch(toInsert);
        } catch (DuplicateKeyException e) {
            throw new SystemException(ResultCode.MEMBER_ALREADY_EXISTS);
        }

        List<ProjectMemberDetailVo> vos = toInsert.stream().map(memberConvert::toDetail).toList();
        UserFillHelper.fillUserInfo(vos, userService);
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProjectMembers(@ParameterNotNull ProjectRemoveMembersDto dto, Long currentUserId) {

        ProjectEntity project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        Long ownerId = project.getOwnerId();

        if (dto.getUserIds().contains(ownerId)) {
            throw new SystemException("不能移除项目所有者", ResultCode.CAN_NOT_REMOVE_MEMBER.getCode());
        }
        if (dto.getUserIds().contains(currentUserId)) {
            throw new SystemException("不能移除自己，请使用退出项目功能", ResultCode.CAN_NOT_REMOVE_MEMBER.getCode());
        }

        // 1. 直接执行删除操作
        // MyBatis-Plus 的 delete 会返回受影响的行数
        LambdaQueryWrapper<ProjectMemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMemberEntity::getProjectId, dto.getProjectId())
                .in(ProjectMemberEntity::getUserId, dto.getUserIds());

        int deletedRows = this.baseMapper.delete(wrapper);

        // 2. 只有当一个都没删掉时，才抛出异常
        // 这通常意味着传入的 userId 都不在项目中，或者已经被其他人删除了
        if (deletedRows == 0) {
            throw new SystemException(ResultCode.MEMBER_NOT_FOUND);
        }

        // 如果 deletedRows > 0 且 < dto.getUserIds().size()，
        // 说明部分成员已不存在，但在“允许部分删除”的逻辑下，我们视作成功。
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectMemberDetailVo updateMemberRole(@ParameterNotNull ProjectUpdateMemberRoleDto dto) {

        ProjectEntity project = projectMapper.selectById(dto.getProjectId());

        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        if (userService.getById(dto.getUserId()) == null) {
            throw new SystemException(ResultCode.USER_NOT_FOUND);
        }

        if (project.getOwnerId().equals(dto.getUserId())) {
            throw new SystemException(ResultCode.CAN_NOT_MODIFY_OWNER);
        }

        LambdaUpdateWrapper<ProjectMemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProjectMemberEntity::getProjectId, dto.getProjectId())
                .eq(ProjectMemberEntity::getUserId, dto.getUserId())
                .set(ProjectMemberEntity::getRole, dto.getRole()
                );

        int updated = this.baseMapper.update(null, wrapper);

        if (updated == 0) {
            throw new SystemException(ResultCode.MEMBER_NOT_FOUND);
        }

        ProjectMemberEntity member = this.lambdaQuery()
                .eq(ProjectMemberEntity::getProjectId, dto.getProjectId())
                .eq(ProjectMemberEntity::getUserId, dto.getUserId())
                .one();

        ProjectMemberDetailVo vo = memberConvert.toDetail(member);
        UserFillHelper.fillUserInfo(Collections.singletonList(vo), userService);
        return vo;
    }

}
