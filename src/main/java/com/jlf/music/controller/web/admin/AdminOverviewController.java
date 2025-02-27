package com.jlf.music.controller.web.admin;

import com.jlf.music.common.Result;
import com.jlf.music.controller.vo.StatisticsVO;
import com.jlf.music.service.AdminOverviewService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/overview")
public class AdminOverviewController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private AdminOverviewService adminOverviewService;

    /**
     * 获取统计数据
     * @return StatisticsVO 歌曲 歌手 专辑 用户 的总数
     */
    @GetMapping("/statistics")
    public Result<StatisticsVO> getOverviewStatistics() {
        return Result.success(adminOverviewService.getOverviewStatistics());
    }
}
