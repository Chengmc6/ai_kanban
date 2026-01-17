package com.example.ai_kanban.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 项目ID参数标记注解
 *
 * 该注解用于标记方法参数中代表项目ID的参数。在进行项目权限校验时，
 * AOP切面会通过反射扫描方法参数，找到被该注解标记的参数，
 * 并将其值作为项目ID用于权限检查。
 *
 * 使用场景：
 * - 标记方法参数中的项目ID字段
 * - 与@ProjectPermission注解配合使用
 * - 支持自动提取项目ID，简化权限校验过程
 *
 * 参数要求：
 * - 参数类型必须是Long
 * - 参数值不能为null
 * - 每个方法中应该只有一个被@ProjectId标记的参数
 *
 * 使用示例：
 * @ProjectPermission(ProjectRoleEnum.ADMIN)
 * public void updateProject(@ProjectId Long projectId, ProjectUpdateDTO dto) {
 *     // projectId会被自动提取用于权限校验
 * }
 *
 * @see ProjectPermission 项目权限校验注解
 * @see com.example.ai_kanban.permission.aspect.ProjectPermissionAspect 权限校验切面
 */
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectId {
    // 该注解仅用于标记参数位置，无需配置属性
}
