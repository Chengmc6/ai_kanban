package com.example.ai_kanban.permission.annotation;

import com.example.ai_kanban.common.enums.ProjectRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 项目权限校验注解
 *
 * 该注解用于标记需要进行项目级别权限校验的方法。当被标记的方法被调用时，
 * AOP切面会自动拦截该方法，并检查当前用户是否具有所需的项目权限。
 *
 * 使用场景：
 * - 用于保护需要项目操作权限的业务方法
 * - 确保只有具有相应角色的项目成员才能执行操作
 * - 与@ProjectId注解配合使用，自动提取项目ID进行权限校验
 *
 * 使用示例：
 * @ProjectPermission(ProjectRoleEnum.ADMIN)
 * public void updateProject(@ProjectId Long projectId, ProjectUpdateDTO dto) {
 *     // 只有项目管理员才能执行此操作
 * }
 *
 * @see ProjectId 项目ID参数标记注解
 * @see com.example.ai_kanban.permission.aspect.ProjectPermissionAspect 权限校验切面
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectPermission {
    /**
     * 所需的项目角色权限
     *
     * 指定访问该方法所需的最低权限等级。系统会检查当前用户在目标项目中的角色，
     * 如果用户的角色权限等级不足，将被拒绝访问。
     *
     * 权限等级说明：
     * - ADMIN：项目管理员，具有所有权限
     * - MANAGER：项目经理，具有管理任务和成员的权限
     * - MEMBER：普通成员，仅可查看和编辑自己的任务
     *
     * @return 所需的项目角色，默认为MEMBER（普通成员）
     */
    ProjectRoleEnum value() default ProjectRoleEnum.MEMBER;

}
