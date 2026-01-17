package com.example.ai_kanban.controller;

import com.example.ai_kanban.common.ApiResponse;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.userdto.AdminUserDetailsVo;
import com.example.ai_kanban.domain.dto.userdto.AdminUserQueryDto;
import com.example.ai_kanban.domain.dto.userdto.AdminUserUpdateDto;
import com.example.ai_kanban.domain.dto.userdto.AdminUserVo;
import com.example.ai_kanban.domain.service.UserService;
import com.example.ai_kanban.security.model.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理 - 用户管理控制器
 * 权限要求：需具备 'ADMIN' 角色
 */
@RestController
@Validated
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 分页查询所有用户列表
     * @param dto 查询过滤条件及分页参数（通过 Query String 传入）
     * @return 包含 AdminUserVo 的分页结果
     */
    @GetMapping
    public ApiResponse<PageResult<AdminUserVo>> getUserList(@RequestBody @NotNull AdminUserQueryDto dto) {
        return ApiResponse.success(userService.getUsersForAdmin(dto));
    }

    /**
     * 获取指定用户的详细信息
     * @param userId 用户 ID
     * @return 用户详细信息视图对象
     */
    @GetMapping("/{userId}")
    public ApiResponse<AdminUserDetailsVo> getUserDetails(@PathVariable @NotNull Long userId) {
        return ApiResponse.success(userService.getUserDetails(userId));
    }

    /**
     * 更新用户状态（启用/禁用/角色变更等）
     * @param userId 被操作的用户 ID
     * @param dto 更新内容
     * @param loginUser 当前登录的管理员信息（用于自保校验）
     */
    @PutMapping("/{userId}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody @NotNull @Valid AdminUserUpdateDto dto,
            @AuthenticationPrincipal LoginUser loginUser) {

        // 禁止管理员禁用或修改自己的账号状态，防止误操作导致无法登录
        if (loginUser.getUserId().equals(userId)) {
            throw new SystemException(ResultCode.CAN_NOT_DISABLE_SELF);
        }

        userService.updateUserStatus(userId, dto);
        return ApiResponse.success("用户状态设置成功");
    }

    /**
     * 永久删除用户
     * @param userId 被删除的用户 ID
     * @param loginUser 当前登录的管理员信息
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> removeUser(
            @PathVariable @NotNull Long userId,
            @AuthenticationPrincipal LoginUser loginUser) {

        // 禁止管理员删除自己的账号
        if (loginUser.getUserId().equals(userId)) {
            throw new SystemException(ResultCode.CAN_NOT_REMOVE_SELF);
        }

        userService.removeUser(userId);
        return ApiResponse.success("删除用户成功");
    }
}