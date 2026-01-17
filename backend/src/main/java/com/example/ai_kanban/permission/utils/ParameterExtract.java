package com.example.ai_kanban.permission.utils;

import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.permission.annotation.ProjectId;

import java.lang.reflect.Field;

/**
 * 参数提取工具类
 * 专门用于从请求参数（单个ID、DTO对象、集合或数组）中识别并提取唯一关联的 projectId
 */
public final class ParameterExtract {

    private ParameterExtract() {}

    /**
     * 核心提取逻辑：支持多种入参结构的解析
     */
    public static Long extractProjectId(Object obj) {
        if (obj == null) return null;

        // 场景 A: 参数本身就是 Long 类型 (例如: deleteProject(Long projectId))
        if (obj instanceof Long) {
            return (Long) obj;
        }

        // 场景 B: 集合类型 (例如: batchUpdate(List<TaskDTO> list))
        if (obj instanceof Iterable<?>) {
            Long result = null;
            for (Object item : (Iterable<?>) obj) {
                result = checkAndMerge(result, extractProjectId(item));
            }
            return result;
        }

        // 场景 C: 数组类型 (例如: deleteTasks(Long[] ids))
        if (obj.getClass().isArray()) {
            Long result = null;
            for (Object item : (Object[]) obj) {
                result = checkAndMerge(result, extractProjectId(item));
            }
            return result;
        }

        // 场景 D: DTO 对象或实体类 (例如: createCard(CardDTO dto))
        return extractFromFields(obj);
    }

    /**
     * 利用反射从对象字段中寻找符合条件的 ProjectId
     */
    private static Long extractFromFields(Object obj) {
        // 排除掉基础包装类型和常用类型，避免无谓的反射
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            return null;
        }

        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            // 匹配原则：字段名为 "projectId" 或者 标记了 @ProjectId 注解
            if ("projectId".equals(field.getName()) || field.isAnnotationPresent(ProjectId.class)) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (value == null) continue;

                    if (value instanceof Long) {
                        return (Long) value;
                    } else {
                        throw new SystemException(
                                "解析错误: 字段 [" + field.getName() + "] 在类 [" + clazz.getSimpleName() + "] 中必须为 Long 类型",
                                ResultCode.BAD_REQUEST.getCode()
                        );
                    }
                } catch (IllegalAccessException e) {
                    throw new SystemException("无法访问字段 projectId",ResultCode.BAD_REQUEST.getCode());
                }
            }
        }
        return null;
    }

    /**
     * 校验并合并 ID：确保在批量处理时，所有的元素都属于同一个项目
     * 防止越权漏洞：例如 A 用户的请求里混合了属于 B 项目的任务 ID
     */
    private static Long checkAndMerge(Long existing, Long current) {
        if (current == null) return existing;
        if (existing == null) return current;
        if (!existing.equals(current)) {
            throw new SystemException(
                    "安全校验失败：单次操作不允许跨项目执行",
                    ResultCode.BAD_REQUEST.getCode()
            );
        }
        return existing;
    }
}