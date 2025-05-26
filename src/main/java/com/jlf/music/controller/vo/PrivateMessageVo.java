package com.jlf.music.controller.vo;

import com.jlf.music.common.enumerate.MessageStatus;
import com.jlf.music.common.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivateMessageVo {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 消息状态
     */
    private Integer status;

    /**
     * 是否已读（0-未读，1-已读）
     */
    private Boolean isRead;

    /**
     * 发送时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}