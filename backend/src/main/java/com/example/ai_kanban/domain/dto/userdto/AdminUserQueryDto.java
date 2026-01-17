package com.example.ai_kanban.domain.dto.userdto;

import lombok.Data;

@Data
public class AdminUserQueryDto {
    private Integer pageNum=1;
    private Integer pageSize=10;
    private Byte status;
    private String keyword;
}
