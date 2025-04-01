package com.jlf.music.socket;

import com.jlf.music.config.RabbitMQConfig;
import com.jlf.music.socket.dto.MessageReadNotificationDTO;
import com.jlf.music.socket.dto.MessageRecallNotificationDTO;
import com.jlf.music.socket.dto.UserStatusMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.jlf.music.common.constant.RabbitMQRoutingKeysConstant.MESSAGE_READ;
import static com.jlf.music.common.constant.RabbitMQRoutingKeysConstant.MESSAGE_RECALL;

/**
 * 消息通知监听服务
 * 用于监听 RabbitMQ 队列中的消息，并根据消息类型处理通知逻辑
 */
@Service
@RequiredArgsConstructor
public class MessageNotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 监听消息已读通知
     * 从 RabbitMQ 的私信队列中消费消息，并根据路由键判断是否为消息已读通知
     * 如果是消息已读通知，则通过 WebSocket 通知消息发送者
     *
     * @param notification 消息已读通知对象
     * @param routingKey   消息的路由键，用于判断消息类型
     */
    @RabbitListener(queues = RabbitMQConfig.PRIVATE_MESSAGE_QUEUE)
    public void handleMessageReadNotification(MessageReadNotificationDTO notification,
                                              @Header("amqp_receivedRoutingKey") String routingKey) {
        // 只处理路由键为 "message.read" 的消息
        if (MESSAGE_READ.equals(routingKey)) {
            // 通过 WebSocket 通知消息发送者，消息已被读取
            simpMessagingTemplate.convertAndSendToUser(
                    notification.getSenderId().toString(), // 目标用户
                    "/queue/messages.read", // 目标队列
                    notification);
        }
    }

    /**
     * 监听消息撤回通知
     * 从 RabbitMQ 的私信队列中消费消息，并根据路由键判断是否为消息撤回通知
     * 如果是消息撤回通知，则通过 WebSocket 通知消息接收者
     *
     * @param notification 消息撤回通知对象
     * @param routingKey   消息的路由键，用于判断消息类型
     */
    @RabbitListener(queues = RabbitMQConfig.PRIVATE_MESSAGE_QUEUE)
    public void handleMessageRecallNotification(MessageRecallNotificationDTO notification,
                                                @Header("amqp_receivedRoutingKey") String routingKey) {
        // 只处理路由键为 "message.recall" 的消息
        if (MESSAGE_RECALL.equals(routingKey)) {
            // 通过 WebSocket 通知消息接收者，消息已被撤回
            simpMessagingTemplate.convertAndSendToUser(
                    notification.getReceiverId().toString(),
                    "/queue/messages.recall",
                    notification);
        }
    }

    /**
     * 监听用户状态变更
     * 从 RabbitMQ 的用户状态队列中消费消息，并通过 WebSocket 广播用户状态变更
     *
     * @param statusMessage 用户状态变更消息对象
     */
    @RabbitListener(queues = RabbitMQConfig.USER_STATUS_QUEUE)
    public void handleUserStatusChange(UserStatusMessageDTO statusMessage) {
        // TODO 这里可能还需要判断
        // 通过 WebSocket 广播用户状态变更
        simpMessagingTemplate.convertAndSend(
                "/topic/user.status", // 广播主题
                statusMessage);
    }
}