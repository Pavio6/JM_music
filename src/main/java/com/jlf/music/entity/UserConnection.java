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
 * 用户连接信实体
 */
@Data
@TableName("user_connection")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConnection {
    @TableId("connection_id")
    private String connectionId;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * WebSocket 会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 是否在线：0-离线，1-在线
     */
    @TableField("is_online")
    private Integer isOnline;

    /**
     * 连接时间
     */
    @TableField("connect_time")
    private LocalDateTime connectTime;

    /**
     * 最后活跃时间
     */
    @TableField("last_active_time")
    private LocalDateTime lastActiveTime;
}