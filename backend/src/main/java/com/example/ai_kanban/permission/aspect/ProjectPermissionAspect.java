package com.example.ai_kanban.permission.aspect;

import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.permission.annotation.ProjectPermission;
import com.example.ai_kanban.permission.utils.ParameterExtract;
import com.example.ai_kanban.permission.utils.PermissionChecker;
import com.example.ai_kanban.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 项目权限校验切面
 * 负责在执行业务逻辑前，从方法参数中提取项目 ID 并验证当前用户权限
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ProjectPermissionAspect {

    private final PermissionChecker permissionChecker;

    @Before("@annotation(permission)")
    public void checkPermission(JoinPoint joinPoint, ProjectPermission permission) {
        // 1. 获取当前登录用户 ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new SystemException(ResultCode.UNAUTHORIZED);
        }

        // 2. 从方法参数中提取 ProjectId
        Long projectId = extractProjectIdFromArgs(joinPoint.getArgs());

        // 3. 校验提取结果
        if (projectId == null) {
            throw new SystemException("无法从请求参数中解析项目ID，请检查字段配置", ResultCode.BAD_REQUEST.getCode());
        }

        // 4. 执行权限检查（查库/缓存校验用户是否拥有该项目的特定权限）
        permissionChecker.checkProjectPermission(projectId, currentUserId, permission.value());
    }

    /**
     * 遍历切点参数，利用工具类尝试提取项目 ID
     */
    private Long extractProjectIdFromArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        for (Object arg : args) {
            if (arg == null) continue;

            // 调用工具类进行深度提取（支持 Long, DTO, 集合, 数组）
            Long extractedId = ParameterExtract.extractProjectId(arg);
            if (extractedId != null) {
                return extractedId;
            }
        }
        return null;
    }
}