package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * 关注目标类型
 */
@Getter
public enum FollowTargetType {
    /**
     * 用户
     */
    USER(0),
    /**
     * 歌手
     */
    SINGER(1);

    private final Integer value;

    FollowTargetType(Integer value) {
        this.value = value;
    }
}
