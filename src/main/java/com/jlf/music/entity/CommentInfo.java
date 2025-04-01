package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.checkerframework.checker.units.qual.Temperature;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 歌曲评论表
 */
@Data
@Accessors(chain = true)
@TableName("comment_info")
public class CommentInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;
    /**
     * 目标类型
     */
    @TableField("target_type")
    private Integer targetType;
    /**
     * 目标ID
     */
    @TableField("target_id")
    private Long targetId;
    @TableField("user_id")
    private Long userId;
    /**
     * 评论内容
     */
    @TableField("content")
    private String content;
    /**
     * 父评论ID
     */
    @TableField("parent_comment_id")
    private Long parentCommentId;
    /**
     * 评论层级 最多三层
     */
    @TableField("level")
    private Integer level;
    /**
     * 评论点赞数量
     */
    @TableField("like_count")
    private Integer likeCount;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;

}
