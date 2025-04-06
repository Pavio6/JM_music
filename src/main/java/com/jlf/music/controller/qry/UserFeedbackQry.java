package com.jlf.music.controller.qry;

import com.jlf.music.common.PageRequest;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.common.enumerate.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description 用户反馈查询条件对象
 * @Author JLF
 * @Date 2025/4/4 15:41
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserFeedbackQry extends PageRequest {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 反馈类型
     */
    private FeedbackType feedbackType;
    /**
     * 反馈处理状态
     */
    private FeedbackProcessingStatus status;
    /**
     * 搜索关键词
     */
    private String keyword;
}
