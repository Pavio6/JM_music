package com.jlf.music.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 撤回消息通知DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRecallNotificationDTO {

    private Long messageId;

    private Long senderId;

    private Long receiverId;
}