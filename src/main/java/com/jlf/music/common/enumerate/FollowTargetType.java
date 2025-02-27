package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * 关注目标类型
 */
@Getter
public enum FollowTargetType {
    USER(0),
    SINGER(1);
    private final Integer value;

    FollowTargetType(Integer value) {
        this.value = value;
    }
}
