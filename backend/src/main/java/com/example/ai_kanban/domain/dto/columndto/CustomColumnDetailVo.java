package com.example.ai_kanban.domain.dto.columndto;

import lombok.Data;

@Data
public class CustomColumnDetailVo {
    private Long id;
    private String name;
    private Integer orderNum;
    private Integer type;
}
