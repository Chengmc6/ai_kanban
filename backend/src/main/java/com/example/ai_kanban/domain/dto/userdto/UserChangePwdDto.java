package com.example.ai_kanban.domain.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangePwdDto {

    @NotBlank(message = "请输入旧密码")
    private String oldPassword;

    @NotBlank(message = "请输入新密码")
    @Size(min = 6,max = 18,message ="密码为6-18位字符")
    private String newPassword;
}
