package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 专辑类型信息表
 * 用于存储专辑类型的相关信息，包括类型名称、创建时间、更新时间以及删除标志。
 */
@Data
@Accessors(chain = true)
@TableName("type_info")
public class TypeInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型ID，主键，自增
     */
    @TableId(value = "type_id", type = IdType.AUTO)
    private Long typeId;

    /**
     * 类型名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志 (0: 未删除, 1: 已删除)，插入时自动填充
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}