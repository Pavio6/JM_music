package com.jlf.music.controller.vo;

import lombok.Data;

import java.time.LocalTime;
@Data
public class SongBasicInfoVo {
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

    private String songFilePath;
    /**
     * 歌手名称
     */
    private String singerName;
    /**
     * 专辑名称
     */
    private String albumName;
}
