package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SingerDetailInfoVo {
    private Long singerId;
    private String singerName;
    private String singerAvatar;
    private String singerBio;
    private String regionName;
    private Long followerCount;
}
