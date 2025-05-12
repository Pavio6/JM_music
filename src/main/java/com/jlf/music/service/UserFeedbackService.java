package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.dto.UserFeedbackDTO;
import com.jlf.music.controller.dto.UserFeedbackReplyDTO;
import com.jlf.music.controller.qry.UserFeedbackQry;
import com.jlf.music.controller.vo.FeedbackInfoVo;
import com.jlf.music.controller.vo.FeedbackStatisticsVo;
import com.jlf.music.controller.vo.UserFeedbackDetailVo;
import com.jlf.music.controller.vo.UserFeedbackListVo;
import com.jlf.music.entity.UserFeedback;

import java.util.List;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/4 15:24
 * @Version 1.0
 */
public interface UserFeedbackService extends IService<UserFeedback> {
    /**
     * 获取反馈信息列表
     */
    IPage<FeedbackInfoVo> getFeedbackList(UserFeedbackQry userFeedbackQry);

    /**
     * 获取反馈信息统计数据
     */
    FeedbackStatisticsVo getFeedbackStatistics();

    /**
     * 添加反馈信息
     */
    Boolean addFeedback(UserFeedbackDTO userFeedbackDTO);

    /**
     * 编辑反馈信息
     */
    Boolean editFeedback(UserFeedbackDTO userFeedbackDTO, Long feedbackId);

    /**
     * 获取用户个人反馈信息列表
     */
    List<UserFeedbackListVo> getPersonalFeedbackList();

    /**
     * 获取反馈信息详情
     */
    UserFeedbackDetailVo getFeedbackDetailById(Long feedbackId);

    /**
     * 删除用户反馈信息
     */
    Boolean deleteByFeedbackId(Long feedbackId);

    /**
     * 获取反馈信息详情 - 管理员端
     */
    FeedbackInfoVo getAdminFeedbackDetailById(Long feedbackId);

    /**
     * 更改反馈状态和回复信息
     */
    Boolean updateFeedbackStatusAndReply(Long feedbackId, UserFeedbackReplyDTO dto);
}
