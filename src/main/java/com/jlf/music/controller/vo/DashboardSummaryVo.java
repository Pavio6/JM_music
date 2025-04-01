package com.jlf.music.controller.vo;

import lombok.Data;

@Data
public class DashboardSummaryVo {
    /**
     * 歌曲总数
     */
    private Long totalSongs;
    /**
     * 专辑总数
     */
    private Long totalAlbums;
    /**
     * 歌手总数
     */
    private Long totalSingers;
    /**
     * 用户总数
     */
    private Long totalUsers;
}
