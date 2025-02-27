package com.jlf.music.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlaylistBasicInfoVo {
    /**
     * 歌单id
     */
    private Long playlistId;
    /**
     * 歌单名称
     */
    private String playlistName;
    /**
     * 歌单封面
     */
    private String playlistCover;
    /**
     * 歌单播放量
     */
    private Long playCount;
}
