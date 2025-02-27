package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

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
     * 目标类型 歌曲/歌单/专辑   0-歌曲 1-歌单 2-专辑
     */
    private Integer targetType;
    /**
     * 目标ID 歌曲/歌单ID
     */
    private Long targetId;
    private Long userId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 父评论ID
     */
    private Long parentCommentId;
    /**
     * 评论层级 最多三层
     */
    private Integer level;
    /**
     * 评论点赞数量
     */
    private Integer likeCount;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

}
