package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/21 18:38
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SongMvVo {
    private Long mvId;
    private LocalDate mvReleaseDate;
    private String songName;
    private Long songId;
    private Long singerId;
    private String singerName;
    private String mvCover;
}
