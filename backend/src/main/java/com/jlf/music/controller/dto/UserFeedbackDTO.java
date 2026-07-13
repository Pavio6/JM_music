package com.jlf.music.controller.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.common.enumerate.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/14 18:47
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedbackDTO {
    /**
     * 反馈类型
     */
    @TableField("feedback_type")
    private FeedbackType feedbackType;

    /**
     * 反馈标题
     */
    @TableField("feedback_title")
    private String feedbackTitle;

    /**
     * 反馈内容
     */
    @TableField("feedback_content")
    private String feedbackContent;
}
