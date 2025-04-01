package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingerVo {
    private Long singerId;
    private String singerName;
    /**
     * 歌手国籍
     */
    private String singerNat;
    /**
     * 歌手简介
     */
    private String singerBio;
    private Integer regionId;
    /**
     * 区域
     */
    private String regionName;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleteFlag;

}
