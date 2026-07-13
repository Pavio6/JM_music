package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * 歌单标签类型
 */
@Getter
public enum PlaylistTagType {
    LANGUAGE("语种"),
    SCENE("场景"),
    THEME("主题"),
    MOOD("心情"),
    GENRE("流派");

    private final String value;

    PlaylistTagType(String value) {
        this.value = value;
    }
}
