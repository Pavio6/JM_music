package com.jlf.music.controller.vo;

import com.jlf.music.entity.TagsInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

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
     * 歌单简介
     */
    private String playlistBio;

    /**
     * 创建者
     */
    private String creatorName;
    /**
     * 歌单播放量
     */
    private Long playCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 歌单状态：0-上架，1-下架
     */
    private Integer status;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    private Integer deleteFlag;

    /**
     * 标签列表（可选）
     */
    private List<TagsInfo> tags;

    /**
     * 歌曲列表（可选）
     */
    private List<PlaylistSongVo> songs;
}
