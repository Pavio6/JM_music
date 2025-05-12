package com.jlf.music.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/13 19:46
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListenTimeVo {
    /**
     * 每日听歌时长
     */
    private Integer listenTime;
}
