package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.common.enumerate.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Description 用户反馈表
 * @Author JLF
 * @Date 2025/4/4 15:09
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("user_feedback")
public class UserFeedback {
    /**
     * 反馈id（主键）
     */
    @TableId(value = "feedback_id", type = IdType.AUTO)
    private Long feedbackId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

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

    /**
     * 处理状态
     */
    @TableField("status")
    private FeedbackProcessingStatus status;

    /**
     * 处理管理员id
     */
    @TableField("admin_id")
    private Long adminId;

    /**
     * 管理员回复
     */
    @TableField("admin_reply")
    private String adminReply;

    /**
     * 回复时间
     */
    @TableField("reply_time")
    private Date replyTime;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志: 0 未删除 1 已删除，插入时自动填充默认值 0
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
