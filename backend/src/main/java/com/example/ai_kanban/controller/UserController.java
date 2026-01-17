package com.example.ai_kanban.controller;

import com.example.ai_kanban.common.ApiResponse;
import com.example.ai_kanban.domain.dto.userdto.*;
import com.example.ai_kanban.domain.service.UserService;
import com.example.ai_kanban.security.model.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块前端控制器
 * 负责用户生命周期管理：登录、注册、资料维护及密码安全
 *
 * @author 高明
 * @since 2025-12-24
 */
@RestController
@Validated
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<UserLoginVO> login(@RequestBody @Valid @NotNull UserLoginRequestDto dto) {
        return ApiResponse.success(userService.login(dto));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid @NotNull UserRegisterDto dto) {
        userService.register(dto);
        return ApiResponse.success("注册成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserInfoVo> getCurrentUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.success(userService.getCurrentUserInfo(loginUser));
    }

    /**
     * 修改个人资料 (非敏感信息)
     */
    @PutMapping("/update")
    public ApiResponse<Void> updateUserInfo(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody @Valid @NotNull UserUpdateDto dto) {
        userService.updateUserInfo(loginUser, dto);
        return ApiResponse.success("资料更新成功");
    }

    /**
     * 修改密码 (高风险操作)
     * 建议在 Service 层配合 LambdaUpdateWrapper 实现
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestBody @Valid @NotNull UserChangePwdDto dto) {
        userService.changePassword(loginUser, dto);
        return ApiResponse.success("密码修改成功，请妥善保管新密码");
    }
}