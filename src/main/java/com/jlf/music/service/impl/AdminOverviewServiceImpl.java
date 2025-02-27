package com.jlf.music.service.impl;

import com.jlf.music.controller.vo.StatisticsVO;
import com.jlf.music.mapper.AlbumInfoMapper;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.mapper.SysUserMapper;
import com.jlf.music.service.AdminOverviewService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jlf.music.common.constant.RedisConstant.*;

@Slf4j
@Service
public class AdminOverviewServiceImpl implements AdminOverviewService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private SingerInfoMapper singerInfoMapper;
    @Resource
    private AlbumInfoMapper albumInfoMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    /**
     * 获取统计数据
     *
     * @return StatisticsVO 歌曲 歌手 专辑 用户 的总数
     */
    @Override
    public StatisticsVO getOverviewStatistics() {
        // 当前类的代理对象
        AdminOverviewService proxy = (AdminOverviewService) AopContext.currentProxy();
        // 尝试从Redis获取数据
        Map<Object, Object> statisticsMap = stringRedisTemplate.opsForHash().entries(STATISTICS_KEY);

        StatisticsVO statistics;
        if (!statisticsMap.isEmpty()) {
            // Redis中有数据，直接返回
            statistics = convertMapToStatistics(statisticsMap);
        } else {
            // Redis中没有数据，从数据库查询并缓存
            statistics = proxy.queryAndCacheStatistics();
        }

        return statistics;
    }

    /**
     * 设置值到StatisticsVo
     * @param map redis查询的结果
     * @return StatisticsVO
     */
    private StatisticsVO convertMapToStatistics(Map<Object, Object> map) {
        StatisticsVO statistics = new StatisticsVO();
        statistics.setTotalSongs(Long.parseLong(map.getOrDefault(SONG_COUNT_KEY, "0").toString()));
        statistics.setTotalSingers(Long.parseLong(map.getOrDefault(SINGER_COUNT_KEY, "0").toString()));
        statistics.setTotalAlbums(Long.parseLong(map.getOrDefault(ALBUM_COUNT_KEY, "0").toString()));
        statistics.setTotalUsers(Long.parseLong(map.getOrDefault(USER_COUNT_KEY, "0").toString()));
        return statistics;
    }

    /**
     * 查询和缓存统计
     */
    @Override
    @Transactional
    public StatisticsVO queryAndCacheStatistics() {
        StatisticsVO statistics = new StatisticsVO();

        // 从数据库查询数据
        Long songCount = songInfoMapper.selectCount(null);
        Long singerCount = singerInfoMapper.selectCount(null);
        Long albumCount = albumInfoMapper.selectCount(null);
        Long userCount = sysUserMapper.selectCount(null);
        // 设置统计数据
        statistics.setTotalSongs(songCount);
        statistics.setTotalSingers(singerCount);
        statistics.setTotalAlbums(albumCount);
        statistics.setTotalUsers(userCount);
        // 缓存到Redis
        try {
            Map<String, String> cacheMap = new HashMap<>();
            cacheMap.put(SONG_COUNT_KEY, String.valueOf(songCount));
            cacheMap.put(SINGER_COUNT_KEY, String.valueOf(singerCount));
            cacheMap.put(ALBUM_COUNT_KEY, String.valueOf(albumCount));
            cacheMap.put(USER_COUNT_KEY, String.valueOf(userCount));
            stringRedisTemplate.opsForHash().putAll(STATISTICS_KEY, cacheMap);
            // 设置过期时间
            stringRedisTemplate.expire(STATISTICS_KEY, CACHE_TIME, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Cache statistics failed", e);
        }
        return statistics;
    }
    // 更新统计数据的方法（可以在添加/删除记录时调用）
    // TODO 在实体类增删改操作时更新缓存
    public void updateStatistics(String type, long count) {
        try {
            String key = switch (type) {
                case "song" -> SONG_COUNT_KEY;
                case "singer" -> SINGER_COUNT_KEY;
                case "album" -> ALBUM_COUNT_KEY;
                case "user" -> USER_COUNT_KEY;
                default -> throw new IllegalArgumentException("Invalid type: " + type);
            };
            stringRedisTemplate.opsForHash().put(STATISTICS_KEY, key, String.valueOf(count));
        } catch (Exception e) {
            log.error("Update statistics failed", e);
        }
    }
}
