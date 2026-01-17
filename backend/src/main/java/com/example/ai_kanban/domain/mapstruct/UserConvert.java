package com.example.ai_kanban.domain.mapstruct;

import com.example.ai_kanban.domain.dto.userdto.*;
import com.example.ai_kanban.domain.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserConvert {
    UserInfoVo toUserInfo(UserEntity user);
    UserEntity userRegisterDtoToEntity(UserRegisterDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDto dto,@MappingTarget UserEntity user);

    AdminUserVo toAdminUserVo(UserEntity user);
    AdminUserDetailsVo toUserDetailsVo(UserEntity user);

    UserSimpleVo toSimpleVo(UserEntity user);
}
