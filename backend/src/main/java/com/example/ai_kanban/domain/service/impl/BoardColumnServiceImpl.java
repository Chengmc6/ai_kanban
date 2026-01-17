package com.example.ai_kanban.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ai_kanban.aop.annotation.ParameterNotNull;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.enums.ColumnConflictType;
import com.example.ai_kanban.common.enums.ColumnTypeEnum;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.domain.dto.columndto.*;
import com.example.ai_kanban.domain.dto.taskdto.TaskBaseVo;
import com.example.ai_kanban.domain.entity.BoardColumnEntity;
import com.example.ai_kanban.domain.entity.ProjectEntity;
import com.example.ai_kanban.domain.mapper.BoardColumnMapper;
import com.example.ai_kanban.domain.mapper.ProjectMapper;
import com.example.ai_kanban.domain.mapstruct.BoardColumnConvert;
import com.example.ai_kanban.domain.service.BoardColumnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_kanban.domain.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 看板列 服务实现类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Service
@RequiredArgsConstructor
public class BoardColumnServiceImpl extends ServiceImpl<BoardColumnMapper, BoardColumnEntity> implements BoardColumnService {

    private final ProjectMapper projectMapper;
    private final TaskService taskService;
    private final BoardColumnConvert columnConvert;

    @Override
    public void initDefaultColumns(Long projectId) {

        List<ColumnTypeEnum> typeEnums = ColumnTypeEnum.defaultColumns();
        List<BoardColumnEntity> columnList = new ArrayList<>();

        int order = 1;
        for (var type : typeEnums) {
            BoardColumnEntity boardColumn = new BoardColumnEntity();
            boardColumn.setProjectId(projectId);
            boardColumn.setName(type.getDescription());
            // 使用递增的 order 确保排序
            boardColumn.setOrderNum(order++);
            boardColumn.setType(type.getCode());
            columnList.add(boardColumn);
        }

        // 使用 MyBatis-Plus 提供的批量保存，减少数据库 IO 往返
        this.saveBatch(columnList);
    }

    @Override
    public List<BoardColumnDetailsVo> getBoardData(Long projectId) {

        // 查询项目下的列
        List<BoardColumnEntity> columns = this.list(new LambdaQueryWrapper<BoardColumnEntity>()
                .eq(BoardColumnEntity::getProjectId, projectId)
                .orderByAsc(BoardColumnEntity::getOrderNum)); // 建议加上排序

        if (columns.isEmpty()) {
            return Collections.emptyList();
        }

        // 提取ID并获取任务
        List<Long> columnIds = columns.stream().map(BoardColumnEntity::getId).toList();
        List<TaskBaseVo> taskBaseVos = taskService.getTaskBaseByColumnIds(projectId,columnIds);

        // 分组
        Map<Long, List<TaskBaseVo>> taskBaseMap = taskBaseVos.stream()
                .collect(Collectors.groupingBy(TaskBaseVo::getColumnId));

        // 填充数据
        return columns.stream().map(col -> {
            BoardColumnDetailsVo vo = columnConvert.toDetails(col);
            // 即使该列没有任务，也给一个空 List 而不是 null
            vo.setTasks(taskBaseMap.getOrDefault(col.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void removeAllProjectColumns(Long projectId) {
        List<BoardColumnEntity> columnEntities = this.list(new LambdaQueryWrapper<BoardColumnEntity>()
                .eq(BoardColumnEntity::getProjectId, projectId));
        if (columnEntities.isEmpty()) return;

        List<Long> columnIds = columnEntities.stream()
                .map(BoardColumnEntity::getId)
                .toList();
        taskService.removeAllTasks(columnIds);

        this.remove(new LambdaQueryWrapper<BoardColumnEntity>().eq(BoardColumnEntity::getProjectId, projectId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomColumnDetailVo creatColumn(@ParameterNotNull CustomAddColumnDto dto) {

        ProjectEntity project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        // 1. 优化查询：只查询 order_num 这一列，减少 SQL 数据传输量
        Integer maxOrderNum = this.lambdaQuery()
                .eq(BoardColumnEntity::getProjectId, dto.getProjectId())
                .orderByDesc(BoardColumnEntity::getOrderNum)
                .last("LIMIT 1") // 显式限制 1 条，提高效率
                .oneOpt()
                .map(BoardColumnEntity::getOrderNum)
                .orElse(0);

        BoardColumnEntity boardColumnEntity = new BoardColumnEntity();
        boardColumnEntity.setProjectId(dto.getProjectId());
        boardColumnEntity.setName(dto.getName());
        boardColumnEntity.setType(ColumnTypeEnum.CUSTOM.getCode());
        boardColumnEntity.setOrderNum(maxOrderNum + 1);

        // 2. 异常处理：DuplicateKeyException 捕获是正确的
        // 确保数据库中有唯一约束
        try {
            this.save(boardColumnEntity);
        } catch (DuplicateKeyException e) {
            // 建议：如果是一个通用的业务异常，可以使用 ResultCode.CONFLICT
            if(ColumnConflictType.NAME.matches(e)){
                throw new SystemException("列名已存在", ResultCode.CONFLICT.getCode());
            }
            if(ColumnConflictType.ORDER.matches(e)){
                throw new SystemException("系统繁忙，请重试", ResultCode.CONFLICT.getCode());
            }
            throw new SystemException("创建列失败，请重试", ResultCode.CONFLICT.getCode());
        }

        return columnConvert.toCustomColumn(boardColumnEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomColumnDetailVo updateColumnName(ColumnNameUpdateDto dto) {

        // 1. 校验项目
        ProjectEntity project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        // 2. 查询列
        BoardColumnEntity column = this.lambdaQuery()
                .eq(BoardColumnEntity::getProjectId, dto.getProjectId())
                .eq(BoardColumnEntity::getId, dto.getId())
                .one();

        if (column == null) {
            throw new SystemException(ResultCode.COLUMN_NOT_FOUND);
        }

        // 3. 系统列不能修改
        if (!column.getType().equals(ColumnTypeEnum.CUSTOM.getCode())) {
            throw new SystemException("不能修改系统列");
        }

        // 4. 列名重复校验
        boolean exists = this.lambdaQuery()
                .eq(BoardColumnEntity::getProjectId, dto.getProjectId())
                .eq(BoardColumnEntity::getName, dto.getName())
                .ne(BoardColumnEntity::getId, dto.getId())
                .exists();

        if (exists) {
            throw new SystemException("列名已存在");
        }

        // ⭐ 5. 设置旧版本号（乐观锁关键步骤）
        column.setVersion(dto.getVersion());
        column.setName(dto.getName());

        // ⭐ 6. updateById 自动校验 version + 自动 version+1
        boolean updated = this.updateById(column);
        if (!updated) {
            throw new SystemException("列已被他人修改，请刷新后重试", ResultCode.CONFLICT.getCode());
        }

        // 7. 返回最新数据
        BoardColumnEntity latest = this.getById(dto.getId());
        return columnConvert.toCustomColumn(latest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeColumns(@ParameterNotNull RemoveColumnsDto dto) {

        ProjectEntity project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        taskService.removeAllTasks(dto.getColumnIds());

        this.lambdaUpdate().eq(BoardColumnEntity::getProjectId,dto.getProjectId())
                .in(BoardColumnEntity::getId,dto.getColumnIds())
                .remove();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortColumns(SortedColumnsDto dto) {

        // 1. 校验项目
        ProjectEntity project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new SystemException(ResultCode.PROJECT_NOT_FOUND);
        }

        // 2. 查询所有列
        List<Long> ids = dto.getColumns().stream().map(SortedColumnsDto.ColumnSortItem::getColumnId).toList();

        List<BoardColumnEntity> columns = lambdaQuery()
                .eq(BoardColumnEntity::getProjectId, dto.getProjectId())
                .in(BoardColumnEntity::getId, ids)
                .list();

        if (columns.size() != dto.getColumns().size()) {
            throw new SystemException("列数据已变更，请刷新重试");
        }

        // 3. 将数据库列放入 map
        Map<Long, BoardColumnEntity> columnMap = columns.stream()
                .collect(Collectors.toMap(BoardColumnEntity::getId, c -> c));

        // 4. 按前端顺序设置排序号 + 设置 version
        int sortNum = 1;
        for (SortedColumnsDto.ColumnSortItem item : dto.getColumns()) {
            BoardColumnEntity column = columnMap.get(item.getColumnId());
            column.setOrderNum(sortNum++);
            column.setVersion(item.getVersion()); // ⭐ 设置旧版本号
        }

        // 5. 执行批量更新（逐条 updateById）
        boolean ok = this.updateBatchById(columns);

        if (!ok) {
            throw new SystemException("排序冲突，请刷新后重试", ResultCode.CONFLICT.getCode());
        }
    }

}
