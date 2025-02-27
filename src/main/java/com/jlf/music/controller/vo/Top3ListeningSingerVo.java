package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Top3ListeningSingerVo {
    private Long singerId;
    private String singerName;
    private String singerAvatar;
    /**
     * 播放该歌手歌曲的听歌时长
     */
    private Integer duration;
}
