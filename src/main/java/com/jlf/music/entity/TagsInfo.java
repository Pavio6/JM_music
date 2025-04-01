package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 歌单标签信息表
 */
@Data
@Accessors(chain = true)
@TableName("tags_info")
public class TagsInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID，主键，自增
     */
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签类型
     */
    @TableField("tag_type")
    private String tagType;

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
     * 删除标志: 0 未删除 1 已删除，插入时自动填充默认值 0
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}