package com.jlf.music.common.constant;


/**
 * RabbitMQ路由键
 *
 * @Date 2025/3/23 17:57
 * @Author JLF
 */
public final class RabbitMQRoutingKeysConstant {
    /**
     * 消息已读通知的路由键
     */
    public static final String MESSAGE_READ = "message.read";
    /**
     * 消息发送通知的路由键
     */
    public static final String MESSAGE_SEND = "message.send";
    /**
     * 消息撤回通知的路由键
     */
    public static final String MESSAGE_RECALL = "message.recall";
    /**
     * 上线状态路由键
     */
    public static final String STATUS_ONLINE = "status.online";
    /**
     * 离线状态路由键
     */
    public static final String STATUS_OFFLINE = "status.offline";

    // 私有构造函数，防止实例化
    private RabbitMQRoutingKeysConstant() {
        throw new UnsupportedOperationException("常量工具类不允许实例化");
    }
}
