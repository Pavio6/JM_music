package com.jlf.music.controller.web.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jlf.music.common.Result;
import com.jlf.music.common.enumerate.FeedbackProcessingStatus;
import com.jlf.music.controller.dto.UserFeedbackReplyDTO;
import com.jlf.music.controller.qry.UserFeedbackQry;
import com.jlf.music.controller.vo.FeedbackInfoVo;
import com.jlf.music.controller.vo.FeedbackStatisticsVo;
import com.jlf.music.service.UserFeedbackService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取反馈信息详情 - 管理员端
     */
    @GetMapping("/{feedbackId}")
    public Result<FeedbackInfoVo> getAdminFeedbackDetail(@PathVariable("feedbackId") Long feedbackId) {
        return Result.success(userFeedbackService.getAdminFeedbackDetailById(feedbackId));
    }

    /**
     * 更改反馈状态和回复信息
     */
    @PostMapping("/reply/{feedbackId}")
    public Result<Boolean> updateFeedbackStatusAndReply(@PathVariable Long feedbackId,
                                                        @RequestBody UserFeedbackReplyDTO dto) {
        return Result.success(userFeedbackService.updateFeedbackStatusAndReply(feedbackId, dto));
    }
}
