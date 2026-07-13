package com.jlf.music.service;

import com.jlf.music.controller.vo.DashboardSummaryVo;
import com.jlf.music.controller.vo.HotSongRankingVo;
import com.jlf.music.controller.vo.SongTypeDistributionVo;

public interface AdminDashboardService {



    /**
     * 获取统计数据
     * @return StatisticsVO 歌曲 歌手 专辑 用户 的总数
     */
    DashboardSummaryVo getDashboardSummary();

    /**
     * 获取歌曲类型分布统计
     */
    SongTypeDistributionVo getSongTypeDistribution();

    /**
     * 获取热门歌曲
     */
    HotSongRankingVo getHotSongRanking(int days);
}
