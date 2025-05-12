package com.jlf.music.service.impl;

import com.jlf.music.controller.vo.*;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.*;
import com.jlf.music.service.AdminDashboardService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jlf.music.common.constant.RedisConstant.*;

@Slf4j
@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
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
    @Resource
    private SongPlayDailyMapper songPlayDailyMapper;



    /**
     * 查询和缓存统计
     */
    @Override
    public DashboardSummaryVo getDashboardSummary() {
        DashboardSummaryVo statistics = new DashboardSummaryVo();
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
        return statistics;
    }


    /**
     * 获取歌曲类型分布统计
     */
    @Override
    public SongTypeDistributionVo getSongTypeDistribution() {
        // 获取分组统计结果
        List<SongTypeDistributionItem> items = albumInfoMapper.getSongTypeDistributionItems();
        // 计算总歌曲数（各分类数量之和）
        long total = items.stream().mapToLong(SongTypeDistributionItem::getCount).sum();
        return new SongTypeDistributionVo()
                .setData(items)
                .setTotal(total);
    }

    /**
     * 获取热门歌曲
     */
    @Override
    public HotSongRankingVo getHotSongRanking(int days) {
        // 参数校验
        if (days <= 0) throw new ServiceException("统计天数必须大于0");

        // 获取当前周期
        LocalDate endDate = LocalDate.now(); // 2025-03-30
        LocalDate startDate = endDate.minusDays(days - 1); // 2025-03-24
        // 获取上一个周期
        LocalDate previousEndDate = startDate.minusDays(1); // 2025-03-23
        LocalDate previousStartDate = previousEndDate.minusDays(days - 1); // 2025-03-17

        // 执行查询
        List<HotSongRankingItem> rankingItems = songPlayDailyMapper.getHotSongRanking(
                startDate, endDate,
                previousStartDate, previousEndDate,
                days
        );

        // 构建响应
        return new HotSongRankingVo()
                .setData(rankingItems)
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString());
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
