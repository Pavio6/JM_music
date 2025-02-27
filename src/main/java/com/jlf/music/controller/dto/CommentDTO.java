package com.jlf.music.controller.dto;

import com.jlf.music.common.enumerate.TargetType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentDTO {
    // 评论类型
    private TargetType targetType;
    private Long targetId;
    private String content;
    private Long parentCommentId;
}
