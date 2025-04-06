package com.jlf.music.common.enumerate;

/**
 * @Description 反馈状态
 * @Author JLF
 * @Date 2025/4/4 15:17
 * @Version 1.0
 */
public enum FeedbackProcessingStatus {
    /**
     * 待处理
     */
    PENDING,
    /**
     * 处理中
     */
    PROCESSING,
    /**
     * 已解决
     */
    RESOLVED,
    /**
     * 已拒绝
     */
    REJECTED
}
