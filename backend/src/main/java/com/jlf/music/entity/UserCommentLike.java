package com.jlf.music.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description 用户喜欢评论
 * @Author JLF
 * @Date 2025/5/14 11:57
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserCommentLike {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 评论id
     */
    private Long commentId;
    /**
     * 创建时间
     */
    private Date createTime;
}
