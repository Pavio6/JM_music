package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * 目标类型 歌曲 歌单 专辑
 */
@Getter
public enum TargetType {
    /**
     * 歌曲
     */
    SONG(0),
    /**
     * 歌单
     */
    PLAYLIST(1),
    /**
     * 专辑
     */
    ALBUM(2),
    /**
     * 歌曲MV
     */
    MV(3);

    private final Integer value;

    TargetType(Integer value) {
        this.value = value;
    }
}
