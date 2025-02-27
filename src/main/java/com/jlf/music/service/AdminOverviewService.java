package com.jlf.music.service;

import com.jlf.music.controller.vo.StatisticsVO;

public interface AdminOverviewService {
    /**
     * 获取统计数据
     * @return StatisticsVO 歌曲 歌手 专辑 用户 的总数
     */
    StatisticsVO getOverviewStatistics();

    /**
     * 查询和统计缓存
     * @return StatisticsVO 歌曲 歌手 专辑 用户 的总数
     */
    StatisticsVO queryAndCacheStatistics();
}
