package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/17 14:22
 * @Version 1.0
 */
@Getter
public enum UserStatus {
    /**
     * 正常
     */
    ENABLE(0),
    /**
     * 停用
     */
    DISABLED(1);

    private final Integer code;

    UserStatus(Integer code) {
        this.code = code;
    }
}
