package com.example.ai_kanban.common.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum ColumnTypeEnum {

    CUSTOM(0,"自定义"),
    TODO(1,"未开始"),
    DOING(2,"进行中"),
    DONE(3,"已完成");

    private final int code;
    private final String description;

    ColumnTypeEnum(int code,String description){
        this.code=code;
        this.description=description;
    }

    public static ColumnTypeEnum fromCode(Integer code) {
        for (ColumnTypeEnum type : values()) {
            if (type.code == code) return type;
        }
        return null;
    }

    public boolean isSystemColumn() {
        return this != CUSTOM;
    }

    public static List<ColumnTypeEnum> defaultColumns() {
        return List.of(TODO, DOING, DONE);
    }

}
