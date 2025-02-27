package com.jlf.music.controller.vo;

import lombok.Data;

@Data
public class SingerBasicInfoVo {
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
}
