package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
@TableName("song_play_daily")
public class SongPlayDaily {
    private Long id;
    private Long songId;
    /**
     * 当日播放量
     */
    private Long playCount;
    /**
     * 统计日期
     */
    private LocalDate date;
}
