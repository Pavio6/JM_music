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
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;
    private String tagName;
    /**
     * 标签类型
     */
    private String tagType;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
