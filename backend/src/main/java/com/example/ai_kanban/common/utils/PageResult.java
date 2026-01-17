package com.example.ai_kanban.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<R>{
    private Long total;
    private Long pageNum;
    private Long pageSize;
    private Long totalPages;
    private List<R> records;

    public static <R> PageResult<R> empty() {
        PageResult<R> result = new PageResult<>();
        result.setTotal(0L);
        result.setPageNum(1L);
        result.setPageSize(0L);
        result.setTotalPages(0L);
        result.setRecords(List.of());
        return result;
    }
}
