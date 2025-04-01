package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * @Author: JLF
 * @Description: xxx
 * @Date 2025/3/21 22:39
 */
@Getter
public enum OnlineStatus {
    ONLINE(1),
    OFFLINE(0);
    private final int value;

    OnlineStatus(int value) {
        this.value = value;
    }

}
