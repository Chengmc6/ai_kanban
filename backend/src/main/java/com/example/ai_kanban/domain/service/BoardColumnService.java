package com.example.ai_kanban.domain.service;

import com.example.ai_kanban.domain.dto.columndto.*;
import com.example.ai_kanban.domain.entity.BoardColumnEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 看板列 服务类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
public interface BoardColumnService extends IService<BoardColumnEntity> {

    void initDefaultColumns(Long projectId);

    List<BoardColumnDetailsVo> getBoardData(Long projectId);

    void removeAllProjectColumns(Long projectId);

    CustomColumnDetailVo creatColumn(CustomAddColumnDto dto);

    CustomColumnDetailVo updateColumnName(ColumnNameUpdateDto dto);

    void removeColumns(RemoveColumnsDto dto);

    void sortColumns(SortedColumnsDto dto);
}
