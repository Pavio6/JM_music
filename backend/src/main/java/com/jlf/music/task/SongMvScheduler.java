package com.jlf.music.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jlf.music.entity.SongMv;
import com.jlf.music.mapper.SongMvMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Set;

import static com.jlf.music.common.constant.MusicConstant.MV_DAILY_PLAY_COUNT_KEY_PREFIX;
import static com.jlf.music.common.constant.MusicConstant.SONG_DAILY_PLAY_COUNT_KEY_PREFIX;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/21 21:17
 * @Version 1.0
 */
@Component
public class SongMvScheduler {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SongMvMapper songMvMapper;

    @Scheduled(cron = "0 2 0 * * ?")
    public void updateSongDailyPlayCount() {
        // 获取所有歌单的播放量
        Set<String> keys = stringRedisTemplate.keys(MV_DAILY_PLAY_COUNT_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                // 获取歌曲ID
                // 将字符串中的PLAYLIST_PLAY_COUNT_KEY_PREFIX 替换为 "" -> 获取歌曲id
                Long mvId = Long.parseLong(key.replace(SONG_DAILY_PLAY_COUNT_KEY_PREFIX, ""));
                // 获取播放量
                String mvDailyPlayCountStr = stringRedisTemplate.opsForValue().get(key);
                // 转为long类型
                long playCount = mvDailyPlayCountStr != null ? Long.parseLong(mvDailyPlayCountStr) : 0;

                // 更新歌曲主表的播放量
                songMvMapper.update(new LambdaUpdateWrapper<SongMv>()
                        .eq(SongMv::getMvId, mvId)
                        .setSql("play_count = play_count + " + playCount));
                // 清空 Redis 中的播放量
                stringRedisTemplate.delete(key);
            }
        }
    }
}
