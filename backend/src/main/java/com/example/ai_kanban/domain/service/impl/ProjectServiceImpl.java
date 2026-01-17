package com.example.ai_kanban.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.common.utils.PageUtils;
import com.example.ai_kanban.common.utils.UserFillHelper;
import com.example.ai_kanban.domain.dto.projectdto.*;
import com.example.ai_kanban.domain.entity.BoardColumnEntity;
import com.example.ai_kanban.domain.entity.ProjectEntity;
import com.example.ai_kanban.domain.entity.ProjectMemberEntity;
import com.example.ai_kanban.domain.mapper.ProjectMapper;
import com.example.ai_kanban.domain.mapstruct.ProjectConvert;
import com.example.ai_kanban.domain.service.*;
import com.example.ai_kanban.aop.annotation.ParameterNotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 项目表 服务实现类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {

    private final ProjectConvert projectConvert;
    private final BoardColumnService boardColumnService;
    private final TaskService taskService;
    private final ProjectMemberService memberService;
    private final UserService userService;

    @Override
    public ProjectEntity executeCreate(Long ownerId, String projectName) {
        ProjectEntity project = new ProjectEntity();
        project.setName(projectName);
        project.setOwnerId(ownerId);

        this.save(project);
        //默认生成3条系统列
        boardColumnService.initDefaultColumns(project.getId());

        //在项目成员表里插入项目id和用户id（默认创建者为拥有者）
        memberService.insertOwnerMember(project.getId(), project.getOwnerId());
        return project;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectBaseVo createProject(@ParameterNotNull Long ownerId, @ParameterNotNull ProjectCreateDto dto) {

        ProjectEntity project = executeCreate(ownerId, dto.getProjectName());

        return projectConvert.toProjectBase(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectBaseVo batchCreateProject(Long ownerId,@ParameterNotNull AiBatchCreateProjectDto dto) {

        ProjectEntity project=executeCreate(ownerId,dto.getProjectName());

        // =====  获取已有的列（系统列） =====
        List<BoardColumnEntity> existingColumns = boardColumnService.list(
                new LambdaQueryWrapper<BoardColumnEntity>()
                        .eq(BoardColumnEntity::getProjectId, project.getId())
                        .orderByAsc(BoardColumnEntity::getOrderNum)
        );

        if(!dto.getColumns().isEmpty()){

            // 创建列名 -> 列实体的映射
            Map<String, BoardColumnEntity> columnMap = existingColumns.stream()
                    .collect(Collectors.toMap(
                            BoardColumnEntity::getName,
                            col -> col,
                            (existing, replacement) -> existing  // 如果重复，保留第一个
                    ));
            // ===== 5. 处理 AI 生成的列和任务 =====
            int nextOrderNum = existingColumns.size() + 1;  // 从4开始

            for(var item : dto.getColumns()){

                String columnName=item.getColumnName();
                BoardColumnEntity targetColumn;

                if(columnMap.containsKey(columnName)){
                    targetColumn=columnMap.get(columnName);
                }else{
                    BoardColumnEntity newColumn=new BoardColumnEntity();
                    newColumn.setProjectId(project.getId());
                    newColumn.setName(columnName);
                    newColumn.setOrderNum(nextOrderNum++);

                    targetColumn = newColumn;
                    columnMap.put(columnName, newColumn);
                }

                if(!item.getTasks().isEmpty()){
                    taskService.createTasksFromAI(project.getId(),
                            targetColumn.getId(),
                            item.getTasks(),
                            dto.getIsAiGenerated(),
                            dto.getAiPrompt(),
                            dto.getAiModelInfo());
                }
            }
        }

        return projectConvert.toProjectBase(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailsVo getProjectDetail(@ParameterNotNull Long projectId) {



        ProjectEntity project = this.getById(projectId);
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }
        //装配vo
        ProjectDetailsVo vo = projectConvert.toProjectDetail(project);
        UserFillHelper.fillUserInfo(Collections.singletonList(vo), userService);
        vo.setColumns(boardColumnService.getBoardData(project.getId()));
        vo.setMembers(memberService.getMembersVo(project.getId()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDetailsVo updateForProject(@ParameterNotNull ProjectUpdateDto dto) {

        ProjectEntity project = this.getById(dto.getProjectId());
        if (project == null) throw new SystemException(ResultCode.PROJECT_NOT_FOUND);

        boolean isChanged = false;

        // 更新名称
        if (StringUtils.hasText(dto.getName()) && !dto.getName().equals(project.getName())) {
            project.setName(dto.getName());
            isChanged = true;
        }

        // 更新 Owner (核心联动)
        if (dto.getNewUserId() != null && !dto.getNewUserId().equals(project.getOwnerId())) {
            if (userService.getById(dto.getNewUserId()) == null) {
                throw new SystemException("新拥有者不存在", ResultCode.USER_NOT_FOUND.getCode());
            }
            Long oldOwnerId = project.getOwnerId();
            project.setOwnerId(dto.getNewUserId());
            // 联动更新成员表
            memberService.updateOwner(dto.getProjectId(), oldOwnerId, dto.getNewUserId());
            isChanged = true;
        }

        if (isChanged) {
            this.updateById(project);
        }

        // 直接返回最新详情
        return getProjectDetail(dto.getProjectId());//此处内部调用不触发权限校验，因外部已做更高级别校验
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProject(@ParameterNotNull Long projectId) {
        boardColumnService.removeAllProjectColumns(projectId);
        memberService.removeProjectAllMembers(projectId);
        boolean isDeleted = this.removeById(projectId);
        if (!isDeleted) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ProjectBaseVo> projectQuery(Long currentUserId, @ParameterNotNull ProjectQueryDto dto) {

        List<ProjectMemberEntity> members=memberService.lambdaQuery()
                .eq(ProjectMemberEntity::getUserId,currentUserId)
                .list();

        List<Long> projectIds=members.stream()
                .map(ProjectMemberEntity::getProjectId)
                .distinct()
                .toList();
        if (members.isEmpty()) {
            return PageResult.empty();
        }

        Page<ProjectEntity> page=new Page<>(dto.getPageNum(),dto.getPageSize());
        LambdaQueryWrapper<ProjectEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(ProjectEntity::getId,projectIds)
                .like(StringUtils.hasText(dto.getKeyword()),ProjectEntity::getName,dto.getKeyword())
                .orderByDesc(ProjectEntity::getUpdateTime);

        Page<ProjectEntity> usePage=this.page(page,wrapper);

        return PageUtils.build(usePage,projectConvert::toProjectBase);
    }
}
