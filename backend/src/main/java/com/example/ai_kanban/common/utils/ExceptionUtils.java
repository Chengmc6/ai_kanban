package com.example.ai_kanban.common.utils;

import org.springframework.dao.DuplicateKeyException;

public class ExceptionUtils {

    private ExceptionUtils() {}

    /**
     * 是否是“列名重复”冲突
     */
    public static boolean isColumnNameConflict(DuplicateKeyException e) {
        return containsIndexName(e, "uk_project_name");
    }

    /**
     * 是否是“排序号重复”（并发冲突）
     */
    public static boolean isColumnOrderConflict(DuplicateKeyException e) {
        return containsIndexName(e, "uk_project_order");
    }

    /**
     * 通用索引名判断
     */
    private static boolean containsIndexName(DuplicateKeyException e, String indexName) {
        Throwable cause = e.getCause();
        if (cause == null) {
            return false;
        }
        String message = cause.getMessage();
        return message != null && message.contains(indexName);
    }
}

