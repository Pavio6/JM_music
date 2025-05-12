package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SingerDetailInfoVo {
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
    /**
     * 歌手简介
     */
    private String singerBio;
    /**
     * 地域名称
     */
    private String regionName;
    /**
     * 歌手粉丝数
     */
    private Long followerCount;
    /**
     * 热门歌曲列表
     */
    private List<SongBasicInfoVo> songs;
    /**
     * 专辑列表
     */
    private List<AlbumSearchVo> albums;
}
