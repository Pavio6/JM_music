package com.jlf.music.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jlf.music.entity.PlaylistInfo;
import com.jlf.music.service.PlaylistInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.jlf.music.common.constant.PlaylistConstant.PLAYLIST_PLAY_COUNT_KEY_PREFIX;

/**
 * 歌单 Scheduler
 */
@Slf4j
@Component
public class PlaylistScheduler {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PlaylistInfoService playlistInfoService;

    /**
     * 每天0点更新数据库中歌单的播放量
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updatePlaylistPlayCount() {
        // 获取所有歌单的播放量
        Set<String> keys = stringRedisTemplate.keys(PLAYLIST_PLAY_COUNT_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                // 获取歌单ID
                // 将字符串中的PLAYLIST_PLAY_COUNT_KEY_PREFIX 替换为 ""
                Long playlistId = Long.parseLong(key.replace(PLAYLIST_PLAY_COUNT_KEY_PREFIX, ""));
                // 获取播放量
                String playCountStr = stringRedisTemplate.opsForValue().get(key);
                // 转为long类型
                long playCount = playCountStr != null ? Long.parseLong(playCountStr) : 0;
                // 更新数据库
                playlistInfoService.update(new LambdaUpdateWrapper<PlaylistInfo>()
                        .eq(PlaylistInfo::getPlaylistId, playlistId)
                        .setSql("play_count = play_count + " + playCount));
                // 清空 Redis 中的播放量
                stringRedisTemplate.delete(key);
            }
        }
    }
}
