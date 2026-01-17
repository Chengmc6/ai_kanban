package com.example.ai_kanban.domain.mapstruct;

import com.example.ai_kanban.domain.dto.taskdto.TaskBaseVo;
import com.example.ai_kanban.domain.dto.taskdto.TaskDetailsVo;
import com.example.ai_kanban.domain.entity.TaskDetailEntity;
import com.example.ai_kanban.domain.entity.TaskEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskConvert {

    TaskBaseVo toBaseVo(TaskEntity taskEntity);

    @Mappings({
            @Mapping(source = "task.updateTime", target = "updateTime"),
            @Mapping(source = "detail.updateTime", target = "detailUpdateTime")
    })
    TaskDetailsVo assemble(TaskEntity task, TaskDetailEntity detail);

}
