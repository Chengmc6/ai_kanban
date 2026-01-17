package com.example.ai_kanban.controller;

import com.example.ai_kanban.common.ApiResponse;
import com.example.ai_kanban.common.enums.ProjectRoleEnum;
import com.example.ai_kanban.domain.dto.columndto.*;
import com.example.ai_kanban.domain.service.BoardColumnService;
import com.example.ai_kanban.permission.annotation.ProjectPermission;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 看板列 前端控制器
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Validated
@RestController
@RequestMapping("/column")
@RequiredArgsConstructor
public class BoardColumnController {

    private final BoardColumnService columnService;

    /**
     * 创建自定义列
     * POST /column
     */
    @PostMapping
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    public ApiResponse<CustomColumnDetailVo> creatColumnForProject(
            @RequestBody @NotNull @Valid CustomAddColumnDto dto) {

        return ApiResponse.success(columnService.creatColumn(dto));
    }

    /**
     * 更新列名称
     * PUT /column/name
     */
    @PutMapping("/name")
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    public ApiResponse<CustomColumnDetailVo> updateProjectColumnName(
            @RequestBody @Valid @NotNull ColumnNameUpdateDto dto) {

        return ApiResponse.success(columnService.updateColumnName(dto));
    }

    /**
     * 删除列（支持批量）
     * DELETE /column
     */
    @DeleteMapping
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    public ApiResponse<Void> removeProjectColumns(
            @RequestBody @Valid @NotNull RemoveColumnsDto dto) {

        columnService.removeColumns(dto);
        return ApiResponse.success("删除成功");
    }

    /**
     * 排序列
     * PUT /column/sort
     */
    @PutMapping("/sort")
    @ProjectPermission(ProjectRoleEnum.ADMIN)
    public ApiResponse<Void> sortProjectColumns(
            @RequestBody @Valid @NotNull SortedColumnsDto dto) {

        columnService.sortColumns(dto);
        return ApiResponse.success();
    }
}

