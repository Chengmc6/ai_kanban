package com.example.ai_kanban.domain.dto.userdto;

import lombok.Data;


@Data
public class AdminUserVo {
    private Long id;
    private String username;
    private String nickname;
    private Byte status;
}
