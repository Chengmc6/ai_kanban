package com.example.ai_kanban.domain.service;

import com.example.ai_kanban.domain.entity.UserRoleEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户-角色关联表 服务类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
public interface UserRoleService extends IService<UserRoleEntity> {
    void saveDefaultRole(Long userId);
}
