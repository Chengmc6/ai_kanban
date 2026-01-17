package com.example.ai_kanban.domain.mapper;

import com.example.ai_kanban.domain.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
public interface UserMapper extends BaseMapper<UserEntity> {
    @Select("SELECT * FROM user WHERE username=#{username} AND is_deleted=0")
    UserEntity selectByUsername(@Param("username")String username);

    List<String> selectRolesByUserId(@Param("userId") Long userId);
}
