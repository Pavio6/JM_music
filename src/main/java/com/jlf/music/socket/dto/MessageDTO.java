package com.jlf.music.socket.dto;

import com.jlf.music.common.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息传输DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    /**
     * 消息的唯一标识符
     */
    private Long messageId;

    /**
     * 发送者的用户ID
     */
    private Long senderId;

    /**
     * 发送者的用户名
     */
    private String senderName;

    /**
     * 发送者的头像URL
     */
    private String senderAvatar;

    /**
     * 接收者的用户ID
     */
    private Long receiverId;

    /**
     * 接收者的用户名
     */
    private String receiverName;

    /**
     * 接收者的头像URL
     */
    private String receiverAvatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型（例如：文本、图片、文件等）
     */
    private MessageType messageType;

    /**
     * 消息状态（例如：已发送、已接收、已读等）
     */
    private Integer status;

    /**
     * 标记消息是否已被读取
     */
    private Boolean isRead;

    /**
     * 消息的创建时间
     */
    private LocalDateTime createTime;
}