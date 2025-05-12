package com.jlf.music.controller.web.user;

import com.jlf.music.common.Result;
import com.jlf.music.controller.dto.UserFeedbackDTO;
import com.jlf.music.controller.vo.UserFeedbackDetailVo;
import com.jlf.music.controller.vo.UserFeedbackListVo;
import com.jlf.music.service.UserFeedbackService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 用户反馈信息
 * @Author JLF
 * @Date 2025/4/14 18:45
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user/feedback")
public class UserFeedbackController {
    @Resource
    private UserFeedbackService userFeedbackService;

    /**
     * 添加反馈信息
     */
    @PostMapping("/add")
    public Result<Boolean> addFeedback(@RequestBody UserFeedbackDTO userFeedbackDTO) {
        return Result.success(userFeedbackService.addFeedback(userFeedbackDTO));
    }

    /**
     * 编辑反馈信息
     */
    @PutMapping("/edit/{feedbackId}")
    public Result<Boolean> editFeedback(@RequestBody UserFeedbackDTO userFeedbackDTO, @PathVariable("feedbackId") Long feedbackId) {
        return Result.success(userFeedbackService.editFeedback(userFeedbackDTO, feedbackId));
    }

    /**
     * 查看用户个人反馈列表
     */
    @GetMapping("/list")
    public Result<List<UserFeedbackListVo>> list() {
        return Result.success(userFeedbackService.getPersonalFeedbackList());
    }

    /**
     * 查看反馈信息详情
     */
    @GetMapping("/{feedbackId}")
    public Result<UserFeedbackDetailVo> detail(@PathVariable("feedbackId") Long feedbackId) {
        return Result.success(userFeedbackService.getFeedbackDetailById(feedbackId));
    }

    /**
     * 删除反馈信息
     */
    @DeleteMapping("/delete/{feedbackId}")
    public Result<Boolean> delete(@PathVariable("feedbackId") Long feedbackId) {
        return Result.success(userFeedbackService.deleteByFeedbackId(feedbackId));
    }

}
