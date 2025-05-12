package com.jlf.music.controller.dto;

import com.jlf.music.controller.vo.SongBasicInfoVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 歌单详细信息dto
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDetailDTO {
    private Long playlistId;
    private String playlistName;
    private String playlistBio;
    private String playlistCover;
    private Long playCount;
    private Long userId;
    /**
     * 创建者名称
     */
    private String userName;
    /**
     * 标签名称列表
     */
    private List<String> tagName;
    /**
     * 是否被收藏
     */
    private Boolean isCollected;
    /**
     * 歌曲列表
     */
    private List<SongBasicInfoVo> songs;
}
