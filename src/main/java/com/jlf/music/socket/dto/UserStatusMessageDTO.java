package com.jlf.music.socket.dto;

import com.jlf.music.common.enumerate.OnlineStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户状态消息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusMessageDTO {

    private Long userId;

    private OnlineStatus onlineStatus;
}