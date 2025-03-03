package com.jlf.music.task;

import com.jlf.music.entity.SongInfo;
import com.jlf.music.entity.SongPlayDaily;
import com.jlf.music.mapper.SongPlayDailyMapper;
import com.jlf.music.service.SongInfoService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.RedisConstant.SONG_DAILY_KEY_PREFIX;

/**
 * 歌曲 scheduler
 */
@Component
public class SongScheduler {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SongInfoService songInfoService;
    @Resource
    private SongPlayDailyMapper songPlayDailyMapper;

    /**
     * 歌曲热歌榜 按播放量
     * 每天凌晨1点执行衰减计算
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyDecayTask() {
        // 获取昨日日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateKey = yesterday.format(DateTimeFormatter.BASIC_ISO_DATE);
        String dailyHashKey = SONG_DAILY_KEY_PREFIX + dateKey;
        // 处理飙升榜数据
        handleRisingStats(yesterday, dailyHashKey);
        // 处理热歌榜衰减
        applyHotDecay(dailyHashKey);
    }

    private void handleRisingStats(LocalDate yesterday, String dailyHashKey) {
        // 1. 获取昨天所有歌曲的播放量
        Map<Object, Object> playCounts = stringRedisTemplate.opsForHash().entries(dailyHashKey);
        if (playCounts.isEmpty()) return;
        // 2. 构建统计数据
        List<SongPlayDaily> stats = playCounts.entrySet().stream()
                .map(entry -> new SongPlayDaily()
                        .setSongId(Long.parseLong(entry.getKey().toString()))
                        .setPlayCount(Long.parseLong(entry.getValue().toString()))
                        .setDate(yesterday)
                )
                .collect(Collectors.toList());

        // 3. 批量写入数据库
        songPlayDailyMapper.batchInsert(stats);
    }

    private void applyHotDecay(String dailyHashKey) {
        // 1. 获取所有歌曲的播放量
        Map<Object, Object> playCounts = stringRedisTemplate.opsForHash().entries(dailyHashKey);
        if (playCounts.isEmpty()) return;

        // 2. 计算衰减后的总播放量
        List<SongInfo> updateList = playCounts.entrySet().stream()
                .map(entry -> {
                    Long songId = Long.parseLong(entry.getKey().toString());
                    int dailyPlay = Integer.parseInt(entry.getValue().toString());

                    // 查询当前总播放量
                    SongInfo song = songInfoService.getById(songId);
                    double newCount = song.getPlayCount() * 0.5 + dailyPlay; // 衰减公式

                    return new SongInfo()
                            .setSongId(songId)
                            .setPlayCount(newCount);
                })
                .collect(Collectors.toList());

        // 3. 批量更新数据库
        songInfoService.updateBatchById(updateList);
        // 4. 删除Redis记录
        stringRedisTemplate.delete(dailyHashKey);
    }

}
