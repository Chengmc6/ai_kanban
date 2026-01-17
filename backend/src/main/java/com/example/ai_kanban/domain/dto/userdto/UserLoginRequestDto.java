package com.example.ai_kanban.domain.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @NotBlank(message = "请输入用户名")
    private String username;
    @NotBlank(message = "请输入密码")
    private String password;
}
