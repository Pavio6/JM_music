package com.jlf.music.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息已读通知DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadNotificationDTO {
    
    private Long userId;
    
    private Long senderId;
}