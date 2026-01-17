package com.example.ai_kanban.domain.dto.userdto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String nickname;
    private String email;
    private String avatar;
}
