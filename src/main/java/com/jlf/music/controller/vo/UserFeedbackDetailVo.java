package com.jlf.music.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.common.enumerate.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/14 19:09
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedbackDetailVo {
    /**
     * 反馈id
     */
    private Long feedbackId;
    /**
     * 反馈标题
     */
    private String feedbackTitle;
    /**
     * 反馈类型
     */
    private FeedbackType feedbackType;
    /**
     * 反馈内容
     */
    private String feedbackContent;
    /**
     * 处理状态
     */
    private FeedbackProcessingStatus status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date replyTime;
    /**
     * 管理员回复信息
     */
    private String adminReply;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 管理员名称
     */
    private String adminName;
}
