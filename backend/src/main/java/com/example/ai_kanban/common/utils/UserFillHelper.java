package com.example.ai_kanban.common.utils;

import com.example.ai_kanban.common.interfaces.UserAttachable;
import com.example.ai_kanban.domain.entity.UserEntity;
import com.example.ai_kanban.domain.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserFillHelper {

    public static <T extends UserAttachable> void fillUserInfo(List<T> list, UserService userService){

        if (list == null || list.isEmpty()) return;

        // 1. 收集 userId
        Set<Long> userIds = list.stream()
                .map(UserAttachable::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (userIds.isEmpty()) return;

        // 2. 批量查用户 (考虑到 MyBatis Plus In 查询限制，如果 ID 极多建议分批，目前先规避过时方法)

        List<UserEntity> users=BatchUtils.batchQuery(userIds, userService::listByIds);

        if (users.isEmpty()) return;

        Map<Long, UserEntity> userMap = users.stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));

        // 3. 填充
        list.forEach(item->{
            UserEntity user=userMap.get(item.getUserId());
            if(user!=null){
                item.setNickname(user.getNickname());
                item.setAvatar(user.getAvatar());
            }
        });
    }

}
