package com.example.ai_kanban.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageUtils {

    public static <T,R> PageResult<R> build(Page<T> page, Function<T,R> mapper){
        List<R> dtoList=page.getRecords().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new PageResult<>(
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getPages(),
                dtoList
        );
    }
}
