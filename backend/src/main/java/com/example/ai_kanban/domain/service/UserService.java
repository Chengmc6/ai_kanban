package com.example.ai_kanban.domain.service;

import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.domain.dto.userdto.*;
import com.example.ai_kanban.domain.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ai_kanban.security.model.LoginUser;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
public interface UserService extends IService<UserEntity> {
    UserLoginVO login(UserLoginRequestDto dto);

    void register(UserRegisterDto dto);

    UserInfoVo getCurrentUserInfo(LoginUser loginUser);

    void updateUserInfo(LoginUser loginUser, UserUpdateDto dto);

    void changePassword(LoginUser loginUser, UserChangePwdDto dto);

    PageResult<AdminUserVo> getUsersForAdmin(AdminUserQueryDto dto);

    AdminUserDetailsVo getUserDetails(Long userId);

    void updateUserStatus(Long userId, AdminUserUpdateDto dto);

    void removeUser(Long userId);

    PageResult<UserSimpleVo> getUsersForProject(UserForProjectQueryDto dto, List<Long> memberIds);
}
