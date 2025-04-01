package com.jlf.music.task;

import com.jlf.music.service.AdminDashboardService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StatisticsScheduleTask {
    @Resource
    private AdminDashboardService adminDashboardService;

    // 每天凌晨2点更新缓存
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshStatistics() {
        try {
            adminDashboardService.queryAndCacheStatistics();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}