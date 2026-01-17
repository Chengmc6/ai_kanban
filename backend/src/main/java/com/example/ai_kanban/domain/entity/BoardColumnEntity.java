package com.example.ai_kanban.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 看板列
 * </p>
 *
 * @author 高明
 * @since 2025-12-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("board_column")
public class BoardColumnEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 列名称
     */
    @TableField("name")
    private String name;

    /**
     * 排序序号
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 逻辑删除
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 列类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 列版本
     */
    @Version
    @TableField("version")
    private Long version;
}
