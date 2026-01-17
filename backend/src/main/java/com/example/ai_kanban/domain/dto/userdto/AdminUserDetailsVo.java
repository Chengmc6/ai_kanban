package com.example.ai_kanban.domain.dto.userdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserDetailsVo {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private Byte status;
    private LocalDateTime createTime;
}
