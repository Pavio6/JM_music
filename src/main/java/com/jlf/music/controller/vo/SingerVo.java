package com.jlf.music.controller.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SingerVo {
    private Long singerId;
    /**
     * 歌手国籍
     */
    private String singerNat;
    private String singerName;
    /**
     * 性别 0 女 1 男
     */
    private Integer singerSex;
    /**
     * 出生日期
     */
    private LocalDate singerBirth;
    /**
     * 出道日期
     */
    private LocalDate singerDebutDate;
    /**
     * 歌曲头像
     */
    private String singerAvatar;

}
