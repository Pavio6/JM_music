package com.jlf.music.controller.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AlbumVo {
    private Long albumId;
    private String albumName;
    /**
     * 专辑发行日期
     */
    private LocalDate albumReleaseDate;
    /**
     * 专辑简介
     */
    private String albumBio;
    /**
     * 专辑封面图
     */
    private String albumCover;
    private String typeName;
    private String singerName;
}
