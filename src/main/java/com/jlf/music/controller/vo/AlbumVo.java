package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Long typeId;
    private String typeName;
    private Long singerId;
    private String singerName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
