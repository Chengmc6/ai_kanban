package com.example.ai_kanban.domain.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserLoginVO {
    private String username;
    private String token;
}
