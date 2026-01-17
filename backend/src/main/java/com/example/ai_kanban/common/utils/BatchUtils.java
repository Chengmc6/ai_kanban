package com.example.ai_kanban.common.utils;

import java.util.*;
import java.util.function.Function;

public class BatchUtils {

    /**
     * 通用分批查询工具
     *
     * @param ids        需要查询的 ID 列表
     * @param batchSize  每批大小（建议 500~1000）
     * @param queryFunc  查询函数：接收 List<ID>，返回 List<T>
     * @return 合并后的查询结果
     */
    public static <ID, T> List<T> batchQuery(
            Collection<ID> ids,
            int batchSize,
            Function<List<ID>, List<T>> queryFunc
    ) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<ID> idList = new ArrayList<>(ids);
        List<T> result = new ArrayList<>();

        for (int i = 0; i < idList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, idList.size());
            List<ID> batch = idList.subList(i, end);

            List<T> batchResult = queryFunc.apply(batch);
            if (batchResult != null && !batchResult.isEmpty()) {
                result.addAll(batchResult);
            }
        }

        return result;
    }

    /**
     * 默认批大小 1000
     */
    public static <ID, T> List<T> batchQuery(
            Collection<ID> ids,
            Function<List<ID>, List<T>> queryFunc
    ) {
        return batchQuery(ids, 1000, queryFunc);
    }
}
