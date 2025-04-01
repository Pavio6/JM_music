package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 歌曲信息表
 * 用于存储歌曲的基本信息，包括歌曲名称、时长、歌词、发行日期、文件路径、封面、歌手ID、专辑ID、播放量等。
 */
@Data
@Accessors(chain = true)
@TableName("song_info")
@AllArgsConstructor
@NoArgsConstructor
public class SongInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 歌曲ID，主键，自增
     */
    @TableId(value = "song_id", type = IdType.AUTO)
    private Long songId;

    /**
     * 歌曲名称
     */
    @TableField("song_name")
    private String songName;

    /**
     * 歌曲时长
     */
    @TableField("song_duration")
    private LocalTime songDuration;

    /**
     * 歌曲歌词
     */
    @TableField("song_lyrics")
    private String songLyrics;

    /**
     * 歌曲发行日期
     */
    @TableField("song_release_date")
    private LocalDate songReleaseDate;

    /**
     * 歌曲文件路径
     */
    @TableField("song_file_path")
    private String songFilePath;

    /**
     * 歌曲封面路径
     */
    @TableField("song_cover")
    private String songCover;

    /**
     * 歌手ID
     */
    @TableField("singer_id")
    private Long singerId;

    /**
     * 专辑ID
     */
    @TableField("album_id")
    private Long albumId;

    /**
     * 衰减后的播放量
     */
    @TableField("play_count")
    private Double playCount;

    /**
     * 创建时间，插入时自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间，插入和更新时自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志 (0: 未删除, 1: 已删除)，插入时自动填充
     */
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    private Integer deleteFlag;
}