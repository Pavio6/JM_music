package com.jlf.music.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jlf.music.entity.CarouselInfo;
import com.jlf.music.mapper.CarouselInfoMapper;
import com.jlf.music.service.CarouselInfoService;
import com.jlf.music.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description 定时清理过期轮播图
 * @Author JLF
 * @Date 2025/4/29 17:54
 * @Version 1.0
 */
@Component
public class CarouselCleanupScheduler {
    @Resource
    private CarouselInfoService carouselInfoService;
    @Resource
    private FileService fileService;

    /**
     * 每周一凌晨3点执行清理任务
     */
    @Scheduled(cron = "0 0 3 * * MON")
    public void cleanupExpiredCarousels() {
        // 查询所有已过期的轮播图
        List<CarouselInfo> expiredCarousels = carouselInfoService.list(
                new LambdaQueryWrapper<CarouselInfo>()
                        .lt(CarouselInfo::getEndTime, new Date())
        );
        // 删除MinIO中的图片文件
        expiredCarousels.forEach(carousel -> {
            if (carousel.getImageUrl() != null && !carousel.getImageUrl().isEmpty()) {
                fileService.deleteFile(carousel.getImageUrl());
            }
        });
        // 删除数据库记录
        carouselInfoService.removeBatchByIds(expiredCarousels);
    }
}
