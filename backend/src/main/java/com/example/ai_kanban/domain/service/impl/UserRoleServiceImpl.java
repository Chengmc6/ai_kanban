package com.example.ai_kanban.domain.service.impl;

import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.common.enums.UserRoleEnum;
import com.example.ai_kanban.domain.entity.UserRoleEntity;
import com.example.ai_kanban.domain.mapper.UserRoleMapper;
import com.example.ai_kanban.domain.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户-角色关联表 服务实现类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity> implements UserRoleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDefaultRole(Long userId) {
        if(userId==null){
            throw new SystemException("缺少用户id", ResultCode.BAD_REQUEST.getCode());
        }
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(userId);
        userRole.setRoleId(UserRoleEnum.USER.getId());

        this.save(userRole);
    }
}
