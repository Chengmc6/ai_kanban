package com.example.ai_kanban.domain.dto.projectdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectBaseVo {
    private Long id;
    private String name;
    private Long ownerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
