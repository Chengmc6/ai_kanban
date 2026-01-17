package com.example.ai_kanban.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.common.utils.PageResult;
import com.example.ai_kanban.common.utils.PageUtils;
import com.example.ai_kanban.domain.dto.userdto.*;
import com.example.ai_kanban.domain.entity.UserEntity;
import com.example.ai_kanban.domain.mapper.UserMapper;
import com.example.ai_kanban.domain.mapstruct.UserConvert;
import com.example.ai_kanban.domain.service.AuthenticationService;
import com.example.ai_kanban.domain.service.UserRoleService;
import com.example.ai_kanban.domain.service.cache.UserCacheService;
import com.example.ai_kanban.domain.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ai_kanban.aop.annotation.ParameterNotNull;
import com.example.ai_kanban.security.model.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final UserConvert userConvert;
    private final UserCacheService cacheService;
    private final UserRoleService userRoleService;

    @Override
    public UserLoginVO login(@ParameterNotNull UserLoginRequestDto dto) {
        return authenticationService.authenticateUser(dto.getUsername(), dto.getPassword());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(@ParameterNotNull UserRegisterDto dto) {

        // 1. 用户名唯一性校验（exists() 性能最佳）
        if (lambdaQuery().eq(UserEntity::getUsername, dto.getUsername()).exists()) {
            throw new SystemException(ResultCode.USERNAME_EXISTS);
        }

        // 2. 加密密码
        String encodePwd = passwordEncoder.encode(dto.getPassword());

        // 3. 转换 DTO → Entity
        UserEntity user = userConvert.userRegisterDtoToEntity(dto);
        user.setPassword(encodePwd);

        // 4. 插入用户（MyBatis-Plus 会自动回填 ID）
        this.save(user);

        // 5. 绑定默认角色（普通用户）
        userRoleService.saveDefaultRole(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoVo getCurrentUserInfo(LoginUser loginUser) {
        //调用缓存service，缓存有数据就使用缓存，没有就查询并生成缓存
        return cacheService.getUserInfoById(loginUser.getUserId(), loginUser.getRoles());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(LoginUser loginUser, @ParameterNotNull UserUpdateDto dto) {

        UserEntity user = this.getById(loginUser.getUserId());
        if (user == null) {
            throw new SystemException(ResultCode.USER_NOT_FOUND);
        }
        userConvert.updateUserFromDto(dto, user);
        this.updateById(user);

        //清除缓存
        cacheService.evictUserCache(loginUser.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(LoginUser loginUser, @ParameterNotNull UserChangePwdDto dto) {

        UserEntity user = this.getById(loginUser.getUserId());
        if (user == null) {
            throw new SystemException(ResultCode.USER_NOT_FOUND);
        }
        // 1. 校验旧密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new SystemException(ResultCode.PASSWORD_INCORRECT);
        }
        // 2. 防止新旧密码相同
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new SystemException(ResultCode.PASSWORD_REPETITION);
        }
        // 3. 加密新密码
        String newEncodePassword = passwordEncoder.encode(dto.getNewPassword());

        lambdaUpdate()
                .eq(UserEntity::getId, loginUser.getUserId())
                .set(UserEntity::getPassword, newEncodePassword)
                .update();

        cacheService.evictUserCache(loginUser.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AdminUserVo> getUsersForAdmin(@ParameterNotNull AdminUserQueryDto dto) {

        // 1. 构造分页对象 (MyBatis-Plus 提供)
        // 注意：pageNum 和 pageSize 建议在 DTO 中设置默认值（如 1 和 10）
        Page<UserEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 2. 构造查询条件
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();

        // 3. 动态搜索：用户名或昵称模糊匹配
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(UserEntity::getUsername, dto.getKeyword())
                    .or()
                    .like(UserEntity::getNickname, dto.getKeyword())
            );
        }

        // 4. 状态过滤：如 0-禁用，1-正常
        if (dto.getStatus() != null) {
            wrapper.eq(UserEntity::getStatus, dto.getStatus());
        }

        // 5. 排序规则：新注册的用户靠前
        wrapper.orderByDesc(UserEntity::getCreateTime);

        // 6. 执行物理分页查询
        Page<UserEntity> userPage = this.page(page,wrapper);

        // 7. 转换并封装结果
        // 使用 PageUtils 配合转换器方法引用，完成 Entity 到 VO 的投影
        return PageUtils.build(userPage, userConvert::toAdminUserVo);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailsVo getUserDetails(@ParameterNotNull Long userId) {
        UserEntity user = this.getById(userId);
        if(user==null){
            throw new SystemException(ResultCode.USER_NOT_FOUND);
        }
        return userConvert.toUserDetailsVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(@ParameterNotNull Long userId, @ParameterNotNull AdminUserUpdateDto dto) {

        int rows = this.baseMapper.update( null, new LambdaUpdateWrapper<UserEntity>()
                .eq(UserEntity::getId, userId)
                .ne(UserEntity::getStatus, dto.getStatus())// 只有状态不同才更新，避免无效刷新
                .set(UserEntity::getStatus, dto.getStatus())
        );
        if (rows == 0) {
            throw new SystemException(ResultCode.DATA_ALREADY_UPDATED);
        }
        cacheService.evictUserCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(@ParameterNotNull Long userId) {
        int rows = this.baseMapper.deleteById(userId);
        if (rows == 0) {
            throw new SystemException(ResultCode.USER_NOT_FOUND);
        }
        cacheService.evictUserCache(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserSimpleVo> getUsersForProject(UserForProjectQueryDto dto, List<Long> memberIds) {

        Page<UserEntity> page=new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<UserEntity> wrapper=new LambdaQueryWrapper<>();

        if(StringUtils.hasText(dto.getKeyword())){
            wrapper.and(w->w.like(UserEntity::getUsername,dto.getKeyword())
            .or().like(UserEntity::getNickname,dto.getKeyword())
            );
        }

        // 排除已加入项目的用户
        if (memberIds != null && !memberIds.isEmpty()) {
            wrapper.notIn(UserEntity::getId, memberIds);
        }

        wrapper.eq(UserEntity::getStatus,1);

        wrapper.orderByDesc(UserEntity::getCreateTime);

        Page<UserEntity> userPage=this.page(page,wrapper);

        return PageUtils.build(userPage,userConvert::toSimpleVo);
    }

}
