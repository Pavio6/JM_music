package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 专辑类型信息表
 */
@Data
@Accessors(chain = true)
@TableName("type_info")
public class TypeInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "type_id", type = IdType.AUTO)
    private Long typeId;
    private String typeName;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
