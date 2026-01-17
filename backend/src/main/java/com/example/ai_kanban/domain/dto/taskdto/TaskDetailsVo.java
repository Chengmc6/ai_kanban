package com.example.ai_kanban.domain.dto.taskdto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskDetailsVo extends TaskBaseVo{
    private String content;
    private LocalDateTime deadline;
    private Boolean isAiGenerated;
    private LocalDateTime detailUpdateTime;
}
