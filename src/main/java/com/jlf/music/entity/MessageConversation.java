package com.jlf.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话实体
 */
@Data
@TableName("message_conversation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageConversation {
    /**
     * 会话ID
     */
    @TableId(value = "conversation_id", type = IdType.AUTO)
    private Long conversationId;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 对方用户ID
     */
    @TableField("other_user_id")
    private Long otherUserId;
    /**
     * 最后一条消息ID
     */
    @TableField("last_message_id")
    private Long lastMessageId;
    /**
     * 未读消息数
     */
    @TableField("unread_count")
    private Integer unreadCount;
    /**
     * 是否置顶 0-否 1-是
     */
    @TableField("is_top")
    private Boolean isTop;
    /**
     * 最后更新时间
     */
    @TableField("last_update_time")
    private LocalDateTime lastUpdateTime;
}