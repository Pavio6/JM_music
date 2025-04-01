package com.jlf.music.controller.web.admin;

import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.DashboardSummaryVo;
import com.jlf.music.controller.vo.HotSongRankingVo;
import com.jlf.music.controller.vo.SongTypeDistributionVo;
import com.jlf.music.mapper.SongMvMapper;
import com.jlf.music.service.AdminDashboardService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    @Resource
    private AdminDashboardService adminDashboardService;

    /**
     * 获取统计数据
     *
     * @return StatisticsVO 歌曲 歌手 专辑 用户 的总数
     */
    @GetMapping("/dashboardSummary")
    public Result<DashboardSummaryVo> getDashboardSummary() {
        return Result.success(adminDashboardService.getDashboardSummary());
    }

    /**
     * 获取歌曲类型分布统计
     */
    @GetMapping("/songTypeDistribution")
    public Result<SongTypeDistributionVo> getSongTypeDistribution() {
        return Result.success(adminDashboardService.getSongTypeDistribution());
    }

    /**
     * 获取热门歌曲
     */
    @GetMapping("/hotSongRanking")
    public Result<HotSongRankingVo> getHotSongRanking(@RequestParam(defaultValue = "7") int days) {
        return Result.success(adminDashboardService.getHotSongRanking(days));
    }
}
