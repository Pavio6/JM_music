package com.jlf.music.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author JLF
 * @date 2025/3/30 17:21
 * @description xxx
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SongFormDTO {
    /**
     * 歌曲名
     */
    private String songName;
    /**
     * 歌曲时长
     */
    private LocalTime songDuration;
    /**
     * 歌曲发行日期
     */
    private LocalDate songReleaseDate;
    /**
     * 歌手id
     */
    private Long singerId;
    /**
     * 专辑id
     */
    private Long albumId;
    /**
     * 歌曲mv发行日期
     */
    private LocalDate mvReleaseDate;
    /**
     * 歌曲mv简介
     */
    private String mvBio;
}
