package com.example.ai_kanban.domain.service.cache;

import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.domain.dto.userdto.UserInfoVo;
import com.example.ai_kanban.domain.entity.UserEntity;
import com.example.ai_kanban.domain.mapper.UserMapper;
import com.example.ai_kanban.domain.mapstruct.UserConvert;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCacheService {

    private final UserConvert convert;
    private final UserMapper mapper;

    public UserCacheService(UserConvert convert, UserMapper mapper) {
        this.convert = convert;
        this.mapper = mapper;
    }

    /**
     * 查询用户信息：如果缓存有则走缓存，没有则查库并写入缓存
     */
    @Cacheable(value = "userInfo", key = "#userId")
    public UserInfoVo getUserInfoById(Long userId, List<String> roles) {
        UserEntity user = mapper.selectById(userId);
        if (user == null) {
            throw new SystemException(ResultCode.USER_NOT_FOUND);
        }
        UserInfoVo dto = convert.toUserInfo(user);
        dto.setRoles(roles);
        return dto;
    }

    /**
     * 清除指定用户的缓存
     * allEntries = false: 只清除当前 key
     * beforeInvocation = false: 确保数据库更新成功后再清除
     */
    @CacheEvict(value = "userInfo", key = "#userId")
    public void evictUserCache(Long userId){

    }
}
