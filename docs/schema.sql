CREATE DATABASE IF NOT EXISTS ai_kanban CHARACTER SET utf8mb4;
USE ai_kanban;

-- ============================
-- 1. 用户与权限模块
-- ============================

-- 用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除：0正常 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='用户表';

-- 角色表
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    description VARCHAR(100) COMMENT '角色描述',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    INDEX idx_user (user_id),
    INDEX idx_role (role_id)
) ENGINE=InnoDB COMMENT='用户-角色关联表';

-- ============================
-- 2. 看板模块
-- ============================

-- 项目表
CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    owner_id BIGINT NOT NULL COMMENT '项目拥有者ID',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_owner (owner_id)
) ENGINE=InnoDB COMMENT='项目表';

-- 项目成员表
CREATE TABLE project_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    user_id BIGINT NOT NULL COMMENT '成员用户ID',
    role TINYINT DEFAULT 1 COMMENT '项目内角色：1成员 2管理员 3拥有者',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_project_user (project_id, user_id),
    INDEX idx_project (project_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB COMMENT='项目成员表';

-- 看板列
CREATE TABLE board_column (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    project_id BIGINT NOT NULL COMMENT '所属项目ID',
    name VARCHAR(50) NOT NULL COMMENT '列名称',
    order_num INT DEFAULT 0 COMMENT '排序序号',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    type TINYINT NOT NULL DEFAULT 0 COMMENT '列类型：0自定义 1TODO 2DOING 3DONE',
    version BIGINT DEFAULT 0 COMMENT '列版本',
    UNIQUE KEY uk_project_name (project_id, name),
    UNIQUE INDEX uk_project_order (project_id, order_num),
    INDEX idx_project (project_id)
) ENGINE=InnoDB COMMENT='看板列';

-- ============================
-- 3. 任务模块
-- ============================

-- 任务表
CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    column_id BIGINT NOT NULL COMMENT '所属列ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    priority TINYINT DEFAULT 1 COMMENT '优先级：1普通 2重要 3紧急',
    assignee_id BIGINT COMMENT '指派给的用户ID',
    order_num INT DEFAULT 0 COMMENT '排序序号',
    status TINYINT DEFAULT 0 COMMENT '状态：0未开始 1进行中 2已完成',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    project_id BIGINT NOT NULL COMMENT '所属项目ID',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '版本',
    INDEX idx_column (column_id),
    INDEX idx_assignee (assignee_id)，
    INDEX idx_task_project_id()
) ENGINE=InnoDB COMMENT='任务表';

-- 任务详情表
CREATE TABLE task_detail (
    task_id BIGINT PRIMARY KEY COMMENT '任务ID',
    content TEXT COMMENT '任务内容',
    deadline DATETIME COMMENT '截止时间',
    is_ai_generated TINYINT(1) DEFAULT 0 COMMENT '是否AI生成',
    ai_prompt TEXT COMMENT 'AI提示词',
    ai_model_info VARCHAR(50) COMMENT 'AI模型信息',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='任务详情表';

-- ============================
-- 预插入基础角色
-- ============================
INSERT INTO role (role_name, description) VALUES ('ROLE_ADMIN', '系统管理员');
INSERT INTO role (role_name, description) VALUES ('ROLE_USER', '普通用户');
