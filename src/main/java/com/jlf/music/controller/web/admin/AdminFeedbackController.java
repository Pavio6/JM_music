package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.controller.qry.UserFeedbackQry;
import com.jlf.music.controller.vo.FeedbackInfoVo;
import com.jlf.music.controller.vo.FeedbackStatisticsVo;
import com.jlf.music.service.UserFeedbackService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 管理员处理反馈controller
 * @Author JLF
 * @Date 2025/4/4 15:22
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/admin/feedback")
public class AdminFeedbackController {
    @Resource
    private UserFeedbackService userFeedbackService;

    /**
     * 获取反馈信息列表
     */
    @GetMapping("/list")
    public Result<IPage<FeedbackInfoVo>> getFeedbackList(UserFeedbackQry userFeedbackQry) {
        return Result.success(userFeedbackService.getFeedbackList(userFeedbackQry));
    }

    /**
     * 获取反馈信息统计数据
     */
    @GetMapping("/statistics")
    public Result<FeedbackStatisticsVo> getFeedbackStatistics() {
        return Result.success(userFeedbackService.getFeedbackStatistics());
    }
}
