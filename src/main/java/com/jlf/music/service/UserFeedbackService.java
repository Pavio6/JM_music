package com.jlf.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jlf.music.controller.qry.UserFeedbackQry;
import com.jlf.music.controller.vo.FeedbackInfoVo;
import com.jlf.music.controller.vo.FeedbackStatisticsVo;
import com.jlf.music.entity.UserFeedback;

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

}
