package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * @Author JLF
 * @Description xxx
 * @Date 2025/3/16 16:59
 */
@Getter
public enum MessageStatus {
    /**
     * 发送
     */
    SENT(0),
    /**
     * 已送达
     */
    DELIVERED(1),
    /**
     * 已读
     */
    READ(2),
    /**
     * 撤回
     */
    RECALLED(3);

    private final Integer value;

    MessageStatus(Integer value) {
        this.value = value;
    }
}
