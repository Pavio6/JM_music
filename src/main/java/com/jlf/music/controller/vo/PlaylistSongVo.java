package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author JLF
 * @date 2025/4/1 16:07
 * @description xxx
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongVo {
    private Long songId;
    private String songName;
    private String singerName;
    private String albumName;
}
