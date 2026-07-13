package com.jlf.music.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 时间转换器
 */
public class TimeConverter {

    /**
     * 将 LocalTime 类型的歌曲时长转换为秒数
     *
     * @param songDuration 歌曲时长，LocalTime 类型
     * @return 转换后的秒数
     */
    public static int convertToSeconds(LocalTime songDuration) {
        if (songDuration == null) {
            return 0;
        }
        // 获取小时、分钟和秒并计算总秒数
        return songDuration.getHour() * 3600 + songDuration.getMinute() * 60 + songDuration.getSecond();
    }
    /**
     * 获取当前时间到第二天凌晨的剩余秒数
     *
     * @return 剩余秒数
     */
    public static long getSecondsUntilNextMidnight() {
        LocalDateTime now = LocalDateTime.now(); // 当前时间
        LocalDateTime midnight = LocalDate.now().plusDays(1).atStartOfDay(); // 第二天凌晨 0 点
        return Duration.between(now, midnight).getSeconds(); // 计算剩余秒数
    }
}