package com.example.ai_kanban.domain.dto.userdto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserUpdateDto {
    @NotNull(message = "状态不能为空")
    private Byte status;
}
