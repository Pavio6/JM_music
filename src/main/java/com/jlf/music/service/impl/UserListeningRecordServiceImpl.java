package com.jlf.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlf.music.controller.vo.SongRankingDailyVo;
import com.jlf.music.controller.vo.Top3ListeningSingerVo;
import com.jlf.music.entity.SingerInfo;
import com.jlf.music.entity.SongInfo;
import com.jlf.music.entity.UserListeningRecord;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.mapper.SingerInfoMapper;
import com.jlf.music.mapper.SongInfoMapper;
import com.jlf.music.mapper.UserListeningRecordMapper;
import com.jlf.music.service.UserListeningRecordService;
import com.jlf.music.utils.SecurityUtils;
import com.jlf.music.utils.TimeConverter;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.RedisConstant.*;

@Service
public class UserListeningRecordServiceImpl extends ServiceImpl<UserListeningRecordMapper, UserListeningRecord>
        implements UserListeningRecordService {
    @Resource
    private UserListeningRecordMapper userListeningRecordMapper;
    @Resource
    private SongInfoMapper songInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SingerInfoMapper singerInfoMapper;


    /**
     * 记录用户听歌行为
     *
     * @param songId       歌曲id
     * @param playDuration 播放时长
     * @return Boolean
     */
    @Override
    public Boolean recordListening(Long songId, Integer playDuration) {

        SongInfo songInfo = songInfoMapper.selectById(songId);
        if (songInfo == null) {
            throw new ServiceException("这首歌不存在");
        }
        UserListeningRecord userListeningRecord = new UserListeningRecord();
        if (playDuration > TimeConverter.convertToSeconds(songInfo.getSongDuration())) {
            throw new ServiceException("播放时长有误");
        }
        Long userId = SecurityUtils.getUserId();
        userListeningRecord
                .setUserId(userId)
                .setSongId(songId)
                .setPlayDuration(playDuration)
                .setIsComplete(TimeConverter.convertToSeconds(songInfo.getSongDuration()) * 0.95 <= playDuration ? 1 : 0); // 允许5%的误差
        boolean success = userListeningRecordMapper.insert(userListeningRecord) > 0;
        if (success) {
            updateRedisCache(userId, songId, playDuration, songInfo.getSingerId(), songInfo);
        }
        return success;
    }

    /**
     * 获取用户当天的听歌时长
     *
     * @return 时长
     */
    @Override
    public Integer getDailyListeningDuration() {
        // 获取用户id
        Long userId = SecurityUtils.getUserId();
        LocalDate today = LocalDate.now();
        String dailyDurationKey = DAILY_DURATION_KEY_PREFIX + userId + ":" + today;
        // 从redis中查询今日听歌时长
        String duration = stringRedisTemplate.opsForValue().get(dailyDurationKey);

        if (duration != null) {
            return Integer.parseInt(duration);
        }
        // 如果 Redis 中没有缓存，则从数据库查询
        LambdaQueryWrapper<UserListeningRecord> queryWrapper = new LambdaQueryWrapper<>();
        // 查询play_duration字段
        queryWrapper.select(UserListeningRecord::getPlayDuration)
                .eq(UserListeningRecord::getUserId, userId)
                .ge(UserListeningRecord::getCreateTime, today.atStartOfDay()) // ge >=
                .lt(UserListeningRecord::getCreateTime, today.plusDays(1).atStartOfDay()); // lt <
        // 获取所有满足条件的playDuration的值
        List<Object> results = userListeningRecordMapper.selectObjs(queryWrapper);
        // 转换为流 并对时长求和 得出总时长
        int totalDuration = results.stream()
                .mapToInt(obj -> obj != null ? ((Number) obj).intValue() : 0)
                .sum();
        // 将结果缓存到 Redis，设置过期时间为到第二天凌晨 0 点的剩余时间
        long secondsUntilMidnight = TimeConverter.getSecondsUntilNextMidnight();
        // 将结果缓存到 Redis
        stringRedisTemplate.opsForValue().set(dailyDurationKey, String.valueOf(totalDuration), secondsUntilMidnight, TimeUnit.SECONDS);

        return totalDuration;
    }

    /**
     * 获取用户当天听歌最久的三位歌手及其时长
     */
    @Override
    public List<Top3ListeningSingerVo> getTop3ListeningSingerOfDay() {
        // 获取用户id
        Long userId = SecurityUtils.getUserId();
        LocalDate today = LocalDate.now();
        String mostListenedSingersKey = MOST_LISTENED_SINGERS_KEY_PREFIX + userId + ":" + today;
        Map<Object, Object> singerDurationMap = stringRedisTemplate.opsForHash().entries(mostListenedSingersKey);
        // 将结果转换为 List<Top3ListeningSingerVo> 并排序
        // entrySet() 返回一个包含Map中所有键值对（Entry对象）的Set集合
        return singerDurationMap.entrySet().stream()
                .map(entry -> {
                    Long singerId = Long.parseLong(entry.getKey().toString());
                    Integer duration = Integer.parseInt(entry.getValue().toString());
                    // 从数据库中获取歌手的名称和封面图
                    SingerInfo singerInfo = singerInfoMapper.selectById(singerId);
                    return getTop3ListeningSingerVo(singerInfo, singerId, duration);
                })
                .sorted((s1, s2) -> Integer.compare(s2.getDuration(), s1.getDuration())) // 按播放时长降序排序
                .limit(3) // 取前 3 位
                .collect(Collectors.toList());
    }

    /**
     * 获取用户当天听的歌曲排名
     *
     * @return List<SongRankingDailyVo>
     */
    @Override
    public List<SongRankingDailyVo> getSongRankingOfDay() {
        // 获取当前用户的 ID
        Long userId = SecurityUtils.getUserId();
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 构建 Redis 中存储歌曲排名的 key
        String songRankingKey = SONG_RANKING_KEY_PREFIX + userId + ":" + today;
        // 按照分数从高到低的顺序获取指定键下的所有元素及其分数
        // 用于获取指定键下的歌曲排行信息
        Set<ZSetOperations.TypedTuple<String>> songRankingSet = stringRedisTemplate.opsForZSet().reverseRangeWithScores(songRankingKey, 0, -1);
        // 如果没有歌曲排名数据，返回空列表
        if (songRankingSet == null || songRankingSet.isEmpty()) {
            return List.of();
        }

        // 将结果转换为 List<SongRankingDailyVo>
        return songRankingSet.stream()
                .map(tuple -> {
                    // 获取歌曲id
                    Long songId = Long.parseLong(Objects.requireNonNull(tuple.getValue()));
                    // 获取歌曲播放次数
                    Integer playCount = tuple.getScore() != null ? tuple.getScore().intValue() : 0;
                    // 获取song详细信息
                    SongInfo songInfo = songInfoMapper.selectById(songId);
                    // 获取歌曲关联的歌手详细信息
                    SingerInfo singerInfo = singerInfoMapper.selectById(songInfo.getSingerId());
                    String songName = songInfo.getSongName();
                    String singerName = singerInfo != null ? singerInfo.getSingerName() : "";
                    String songCover = songInfo.getSongCover();
                    // 封装返回结果
                    return new SongRankingDailyVo()
                            .setSongId(songId)
                            .setSongName(songName)
                            .setSingerName(singerName)
                            .setSongCover(songCover)
                            .setPlayCount(playCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 封装Top3ListeningSingerVo对象
     *
     * @param singerInfo 歌手信息
     * @param singerId   歌手id
     * @param duration   播放时长
     * @return Top3ListeningSingerVo
     */
    @NotNull
    private Top3ListeningSingerVo getTop3ListeningSingerVo(SingerInfo singerInfo, Long singerId, Integer duration) {
        String singerName = singerInfo != null ? singerInfo.getSingerName() : "";
        String singerAvatar = singerInfo != null ? singerInfo.getSingerAvatar() : "";
        // 创建 Top3ListeningSingerVo 对象
        Top3ListeningSingerVo singerVo = new Top3ListeningSingerVo();
        singerVo.setSingerId(singerId);
        singerVo.setSingerName(singerName);
        singerVo.setSingerAvatar(singerAvatar);
        singerVo.setDuration(duration);
        return singerVo;
    }

    private void updateRedisCache(Long userId, Long songId, Integer playDuration, Long singerId, SongInfo songInfo) {
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 用户今天的听歌时长key
        String dailyDurationKey = DAILY_DURATION_KEY_PREFIX + userId + ":" + today;
        // 用户每日最喜欢的歌手key
        String mostListenedSingersKey = MOST_LISTENED_SINGERS_KEY_PREFIX + userId + ":" + today;
        // 用户每日最喜欢的歌曲
        String songRankingKey = SONG_RANKING_KEY_PREFIX + userId + ":" + today;
        // 更新当天听歌时长 increment 增加key对应的值的大小
        stringRedisTemplate.opsForValue().increment(dailyDurationKey, playDuration);
        // 更新当天听歌最久的歌手
        stringRedisTemplate.opsForHash().increment(mostListenedSingersKey, singerId.toString(), playDuration);
        // 如果歌曲被完整播放，更新当天听的歌曲排名
        if (TimeConverter.convertToSeconds(songInfo.getSongDuration()) * 0.95 <= playDuration) {
            stringRedisTemplate.opsForZSet().incrementScore(songRankingKey, songId.toString(), 1);
        }
        // 将结果缓存到 Redis，设置过期时间为到第二天凌晨 0 点的剩余时间
        long secondsUntilMidnight = TimeConverter.getSecondsUntilNextMidnight();
        // 设置缓存过期时间为第二天0点
        stringRedisTemplate.expire(dailyDurationKey, secondsUntilMidnight, TimeUnit.SECONDS);
        stringRedisTemplate.expire(mostListenedSingersKey, secondsUntilMidnight, TimeUnit.SECONDS);
        stringRedisTemplate.expire(songRankingKey, secondsUntilMidnight, TimeUnit.SECONDS);
    }
}
