package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author JLF
 * @date 2025/3/30 10:30
 * @description 热门歌曲排行项
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotSongRankingItem {
    private Long songId;
    private String songName;
    private String singerName;
    private String albumName;
    /**
     * 总播放量
     */
    private Long totalPlays;
    /**
     * 日均播放量
     */
    private Double averagePlays;
    /**
     * 环比增长率（百分比）
     */
    private Double growthRate;
}
