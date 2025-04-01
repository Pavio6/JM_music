package com.jlf.music.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话传输DTO
 *
 * @Date 2025/3/22 20:06
 * @Author JLF
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long conversationId;

    private Long otherUserId;

    private String otherUserName;

    private String otherUserAvatar;

    private String lastMessage;

    private String messageType;

    private Integer unreadCount;

    private LocalDateTime lastUpdateTime;

    private Boolean isOnline;
}
