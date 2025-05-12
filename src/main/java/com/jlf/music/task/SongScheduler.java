package com.jlf.music.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.entity.SongPlayDaily;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.mapper.SongPlayDailyMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

import static com.jlf.music.common.constant.MusicConstant.SONG_DAILY_PLAY_COUNT_KEY_PREFIX;

/**
 * 歌曲 scheduler
 */
@Component
public class SongScheduler {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SongPlayDailyMapper songPlayDailyMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    /**
     * 歌曲热歌榜 按播放量
     * 每天凌晨1点执行衰减计算
     */
    /*@Scheduled(cron = "0 0 1 * * ?")
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
    }*/

    /**
     * 每日零点将redis中昨天的歌曲每日播放量同步到数据库中
     */
    @Scheduled(cron = "0 2 0 * * ?")
    public void updateSongDailyPlayCount() {
        // 获取所有歌单的播放量
        Set<String> keys = stringRedisTemplate.keys(SONG_DAILY_PLAY_COUNT_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                // 获取歌曲ID
                // 将字符串中的PLAYLIST_PLAY_COUNT_KEY_PREFIX 替换为 "" -> 获取歌曲id
                Long songId = Long.parseLong(key.replace(SONG_DAILY_PLAY_COUNT_KEY_PREFIX, ""));
                // 获取播放量
                String songDailyPlayCountStr = stringRedisTemplate.opsForValue().get(key);
                // 转为long类型
                long playCount = songDailyPlayCountStr != null ? Long.parseLong(songDailyPlayCountStr) : 0;
                // 添加数据到歌曲每日播放量表中
                songPlayDailyMapper.insert(new SongPlayDaily()
                        .setPlayCount(playCount)
                        .setSongId(songId)
                        .setDate(LocalDate.now().minusDays(1)));
                // 更新歌曲主表的播放量
                songInfoMapper.update(new LambdaUpdateWrapper<SongInfo>()
                        .eq(SongInfo::getSongId, songId)
                        .setSql("play_count = play_count + " + playCount));
                // 清空 Redis 中的播放量
                stringRedisTemplate.delete(key);
            }
        }
    }

}
