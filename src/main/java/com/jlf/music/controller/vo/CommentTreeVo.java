package com.jlf.music.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 歌曲评论树型结构
 */
@Data
@Accessors(chain = true)
public class CommentTreeVo {
    /**
     * 评论id
     */
    private Long commentId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 头像
     */
    private String userAvatar;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 评论点赞数
     */
    private Integer likeCount;
    /**
     * 该用户是否点赞了该评论
     */
    private Boolean isLike;
    /**
     * 二级三级评论列表
     */
    private List<CommentTreeVo> children;
}
