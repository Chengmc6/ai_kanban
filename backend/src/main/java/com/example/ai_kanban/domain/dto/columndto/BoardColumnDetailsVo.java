package com.example.ai_kanban.domain.dto.columndto;

import com.example.ai_kanban.domain.dto.taskdto.TaskBaseVo;
import lombok.Data;

import java.util.List;

@Data
public class BoardColumnDetailsVo {
    private Long id;
    private String name;
    private Integer orderNum;
    private Integer type;
    private Long version;

    private List<TaskBaseVo> tasks;
}
