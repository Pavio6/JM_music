package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description 反馈数据vo
 * @Author JLF
 * @Date 2025/4/4 20:57
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatisticsVo {
    /**
     * 待处理数量
     */
    private Long pendingCount;
    /**
     * 处理中数量
     */
    private Long processingCount;
    /**
     * 今日新增数量
     */
    private Long todayCount;
}
