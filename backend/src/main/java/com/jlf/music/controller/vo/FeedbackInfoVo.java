package com.jlf.music.controller.vo;

import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.common.enumerate.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/4 15:57
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackInfoVo {

    private Long feedbackId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 反馈类型
     */
    private FeedbackType feedbackType;

    /**
     * 反馈标题
     */
    private String feedbackTitle;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    /**
     * 处理状态
     */
    private FeedbackProcessingStatus status;
    /**
     * 管理员id
     */
    private Long adminId;
    /**
     * 管理员名称
     */
    private String adminName;

    /**
     * 管理员回复
     */
    private String adminReply;
    /**
     * 反馈时间
     */
    private LocalDateTime replyTime;
    /**
     * 创建时间，插入时自动填充
     */
    private LocalDateTime createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    private LocalDateTime updateTime;
}
