package com.jlf.music.controller.vo;

import com.jlf.music.common.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class PrivateMessageUserVo {
    /**
     * 对方用户ID
     */
    private Long otherUserId;

    /**
     * 对方用户昵称/姓名
     */
    private String otherUserName;

    /**
     * 对方用户头像URL
     */
    private String otherUserAvatar;
    /**
     * 最后一条消息的消息类型
     */
    private MessageType messageType;

    /**
     * 最后一条消息内容摘要
     */
    private String lastMessageContent;

    /**
     * 最后一条消息的发送/接收时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 当前用户未读消息数
     */
    private Integer unreadCount;
    /**
     * 是否在线
     */
    private Boolean online;

}