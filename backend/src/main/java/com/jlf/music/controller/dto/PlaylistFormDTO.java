package com.jlf.music.controller.dto;

import com.jlf.music.controller.vo.PlaylistSongVo;
import com.jlf.music.entity.TagsInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author JLF
 * @date 2025/4/1 22:33
 * @description xxx
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistFormDTO {

    /**
     * 歌单名称
     */
    private String playlistName;
    /**
     * 歌单简介
     */
    private String playlistBio;
    /**
     * 歌单状态
     */
    private Integer status;
    /**
     * 标签列表（可选）
     */
    private List<Long> tagIds;

    /**
     * 歌曲列表（可选）
     */
    private List<Long> songIds;

}
