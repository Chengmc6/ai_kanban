package com.example.ai_kanban.domain.mapstruct;

import com.example.ai_kanban.domain.dto.columndto.BoardColumnDetailsVo;
import com.example.ai_kanban.domain.dto.columndto.CustomColumnDetailVo;
import com.example.ai_kanban.domain.entity.BoardColumnEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardColumnConvert {
    BoardColumnDetailsVo toDetails(BoardColumnEntity boardColumnEntity);
    CustomColumnDetailVo toCustomColumn(BoardColumnEntity boardColumnEntity);
}
