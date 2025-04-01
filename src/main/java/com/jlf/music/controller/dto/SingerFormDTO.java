package com.jlf.music.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author JLF
 * @date 2025/3/31 22:02
 * @description xxx
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SingerFormDTO {
    private String singerName;       // 歌手名称
    private String singerNat;        // 歌手国籍
    private String singerBio;        // 歌手简介
    private LocalDate singerBirth;   // 出生日期
    private LocalDate singerDebutDate; // 出道日期
    private Integer singerSex;       // 性别（0女 1男）
    private Integer regionId;           // 区域ID
}
