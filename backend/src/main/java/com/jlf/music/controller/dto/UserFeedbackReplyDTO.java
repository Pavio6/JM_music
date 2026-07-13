package com.jlf.music.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/15 11:32
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedbackReplyDTO {
    /**
     * 反馈内容
     */
    @JsonProperty("content")
    private String adminReply;

    /**
     * 处理状态
     */
    private FeedbackProcessingStatus status;
}
