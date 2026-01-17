package com.example.ai_kanban.domain.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {

    @NotBlank(message = "请输入用户名")
    private String username;

    @NotBlank(message = "请输入密码")
    @Size(min = 6,max = 18,message ="密码为6-18位字符")
    private String password;
    @NotBlank(message = "请输入昵称")
    private String nickname;
    private String avatar;
    private String email;
}
