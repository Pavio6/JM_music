package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author JLF
 * @Date 2025/5/13 14:01
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistSimpleInfoVo {
    private Long playlistId;
    private String playlistName;
    private String playlistCover;
}
