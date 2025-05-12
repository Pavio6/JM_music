package com.jlf.music.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author JLF
 * @date 2025/3/31 8:26
 * @description 歌曲详情
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SongDetailVo {
    /**
     * 歌曲ID
     */
    private Long songId;

    /**
     * 歌曲名称
     */
    private String songName;

    /**
     * 歌曲时长
     */
    private LocalTime songDuration;

    /**
     * 歌词
     */
    private String songLyrics;

    /**
     * 发行日期
     */
    private LocalDate songReleaseDate;

    /**
     * 歌曲文件路径
     */
    private String songFilePath;

    /**
     * 歌曲封面
     */
    private String songCover;

    /**
     * 歌手ID
     */
    private Long singerId;

    /**
     * 专辑ID
     */
    private Long albumId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 删除标志（0:未删除, 1:已删除）
     */
    private Integer deleteFlag;

    /**
     * 播放量
     */
    private Double playCount;

    /**
     * 关联歌手名称
     */
    private String singerName;

    /**
     * 关联专辑名称
     */
    private String albumName;
}
