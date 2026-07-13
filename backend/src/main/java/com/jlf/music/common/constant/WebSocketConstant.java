package com.jlf.music.common.constant;

/**
 * @Description
 * @Author JLF
 * @Date 2025/5/21 10:18
 * @Version 1.0
 */
public class WebSocketConstant {
    /**
     * 发送消息
     */
    public static final String TYPE_MESSAGE = "message";
    /**
     * 撤回消息
     */
    public static final String TYPE_RECALL = "recall";
    /**
     * 激活聊天窗口
     */
    public static final String TYPE_CHAT_ACTIVE = "chat_active";
    /**
     * 关闭聊天窗口
     */
    public static final String TYPE_CHAT_INACTIVE = "chat_inactive";
    /**
     * 心跳消息
     */
    public static final String TYPE_HEART_BEAT = "heartbeat";
    /**
     * 撤回消息错误提示信息
     */
    public static final String RECALL_MESSAGE_TIMEOUT = "消息已超过2分钟，无法撤回";
}
