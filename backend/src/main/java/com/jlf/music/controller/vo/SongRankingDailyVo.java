package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SongRankingDailyVo {
    private Long songId;
    private String songName;
    private String singerName;
    private String songCover;
    /**
     * 歌曲当日播放次数
     */
    private Integer playCount;
}
