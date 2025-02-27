package com.jlf.music.controller.vo;

import lombok.Data;

@Data
public class StatisticsVO {
    private Long totalSongs;   // 歌曲总数
    private Long totalSingers; // 歌手总数
    private Long totalAlbums;  // 专辑总数
    private Long totalUsers;   // 用户总数
}
