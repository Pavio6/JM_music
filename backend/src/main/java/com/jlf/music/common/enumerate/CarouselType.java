package com.jlf.music.common.enumerate;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/29 17:11
 * @Version 1.0
 */
@Getter
public enum CarouselType {
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
     * 文章
     */
    ARTICLE(3),

    /**
     * 外部链接
     */
    EXTERNAL_LINK(4);

    private final Integer code;

    CarouselType(Integer code) {
        this.code = code;
    }


    /**
     * 根据 int 值获取对应的枚举
     */
    public static CarouselType fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(type -> type.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }


    /**
     * 判断是否是有效的枚举值
     */
    public static boolean isValid(Integer code) {
        return fromCode(code) != null;
    }


    /**
     * 判断是否为歌曲/歌单/专辑类型
     */
    public boolean isMedia() {
        return this == SONG || this == PLAYLIST || this == ALBUM;
    }


    /**
     * 判断是否是文章/外部链接类型
     */
    public boolean isArticleOrExternal() {
        return this == ARTICLE || this == EXTERNAL_LINK;
    }
}
