package com.jlf.music.common.enumerate;

import lombok.Getter;

/**
 * @Description 歌单标签类型
 * @Author JLF
 * @Date 2025/4/28 13:01
 * @Version 1.0
 */
@Getter
public enum TagType {
    /**
     * 语种
     */
    LANGUAGE("语种"),
    /**
     * 场景
     */
    SCENE("场景"),
    /**
     * 主题
     */
    THEME("主题"),
    /**
     * 心情
     */
    MOOD("心情"),
    /**
     * 流派
     */
    GENRE("流派");

    private final String value;

    TagType(String value) {
        this.value = value;
    }
    /**
     * 根据枚举名称获取对应的字符串值
     * @param enumName 枚举名称，如 "LANGUAGE", "MOOD" 等
     * @return 对应的字符串值
     * @throws IllegalArgumentException 如果枚举名称无效
     */
    public static String getValueByName(String enumName) {
        try {
            return TagType.valueOf(enumName).getValue();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的枚举名称: " + enumName);
        }
    }
}
