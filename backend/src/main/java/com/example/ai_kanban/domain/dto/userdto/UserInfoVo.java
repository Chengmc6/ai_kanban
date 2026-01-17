package com.example.ai_kanban.domain.dto.userdto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoVo {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private Byte status;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private List<String> roles;
}
