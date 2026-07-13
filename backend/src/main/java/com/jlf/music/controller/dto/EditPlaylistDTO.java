package com.jlf.music.controller.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class EditPlaylistDTO {
    /**
     * 歌单名称
     */
    private String playlistName;
    /**
     * 歌单简介
     */
    private String playlistBio;
    /**
     * 歌单标签
     */
    private List<Long> tagIds;

}
