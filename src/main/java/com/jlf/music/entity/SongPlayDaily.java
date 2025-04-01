package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 歌曲每日播放量统计表
 * 用于记录歌曲每天的播放量信息。
 */
@Data
@Accessors(chain = true)
@TableName("song_play_daily")
public class SongPlayDaily {

    /**
     * 主键ID
     */
    @TableField("id")
    private Long id;

    /**
     * 歌曲ID
     */
    @TableField("song_id")
    private Long songId;

    /**
     * 当日播放量
     */
    @TableField("play_count")
    private Long playCount;

    /**
     * 统计日期
     */
    @TableField("date")
    private LocalDate date;
}