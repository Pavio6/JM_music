package com.jlf.music.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class SongBasicInfoVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long songId;
    /**
     * 歌曲名称
     */
    private String songName;
    /**
     * 歌曲时长 HH:MM:SS
     */
    private LocalTime songDuration;
    /**
     * 歌曲封面图
     */
    private String songCover;
    /**
     * 发行日期
     */
    private LocalDate songReleaseDate;
    /**
     * 歌手id
     */
    private Long singerId;
    /**
     * 歌手名称
     */
    private String singerName;
    /**
     * 歌手头像
     */
    private String singerAvatar;
    /**
     * 专辑id
     */
    private Long albumId;
    /**
     * 专辑名称
     */
    private String albumName;

    /**
     * 歌曲播放量
     */
    private Double playCount;
    /**
     * MV id
     */
    private Long mvId;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
