package com.jlf.music.socket;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jlf.music.common.constant.RabbitMQRoutingKeysConstant;
import com.jlf.music.common.enumerate.MessageStatus;
import com.jlf.music.config.RabbitMQConfig;
import com.jlf.music.socket.dto.ConversationDTO;
import com.jlf.music.socket.dto.MessageDTO;
import com.jlf.music.socket.dto.MessageRequestDTO;
import com.jlf.music.entity.MessageConversation;
import com.jlf.music.entity.PrivateMessage;
import com.jlf.music.entity.SysUser;
import com.jlf.music.entity.UserConnection;
import com.jlf.music.mapper.MessageConversationMapper;
import com.jlf.music.mapper.PrivateMessageMapper;
import com.jlf.music.mapper.SysUserMapper;
import com.jlf.music.mapper.UserConnectionMapper;
import com.jlf.music.socket.dto.MessageReadNotificationDTO;
import com.jlf.music.socket.dto.MessageRecallNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.RabbitMQRoutingKeysConstant.MESSAGE_SEND;

/**
 * 私信服务
 * 负责处理用户之间的私信消息，包括发送、接收、撤回、标记已读等功能
 */
@Service
@RequiredArgsConstructor
public class PrivateMessageService {

    private final PrivateMessageMapper privateMessageMapper;
    private final MessageConversationMapper messageConversationMapper;
    private final UserConnectionMapper userConnectionMapper;
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SysUserMapper sysUserMapper;

    @Value("${app.message.recall-time-limit}")
    private long recallTimeLimit;

    /**
     * 发送私信
     *
     * @param senderId 发送者用户ID
     * @param request  消息请求对象，包含接收者ID、消息内容等信息
     * @return 发送的私信消息对象
     */

    @Transactional
    public PrivateMessage sendMessage(Long senderId, @NotNull MessageRequestDTO request) {
        // 创建消息
        PrivateMessage message = PrivateMessage.builder()
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .status(MessageStatus.SENT.getValue())
                .isRead(false)
                .build();
        // 保存消息
        privateMessageMapper.insert(message);
        // 更新发送者的会话
        updateConversation(senderId, request.getReceiverId(), message.getMessageId(), false);
        // 更新接收者的会话
        updateConversation(request.getReceiverId(), senderId, message.getMessageId(), true);

        // 检查接收者是否在线
        if (isUserOnline(request.getReceiverId())) {
            // 直接通过WebSocket发送
            simpMessagingTemplate.convertAndSendToUser(
                    request.getReceiverId().toString(),
                    "/queue/messages", // TODO WebSocket订阅的队列在前端中定义
                    convertToDTO(message));

            // 更新消息状态为已送达  - TODO 可能还需要前端ACK确认机制
            message.setStatus(MessageStatus.DELIVERED.getValue());
            privateMessageMapper.updateById(message);
        } /*else {
            // 接收者离线，通过RabbitMQ发送消息
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PRIVATE_MESSAGE_EXCHANGE, // 交换机
                    MESSAGE_SEND, // 路由键
                    message); // 消息内容
        }*/
        return message;
    }

    /**
     * 更新会话
     *
     * @param userId          用户ID
     * @param otherUserId     对方用户ID
     * @param messageId       消息ID
     * @param incrementUnread 是否增加未读计数
     */
    private void updateConversation(Long userId, Long otherUserId, Long messageId, boolean incrementUnread) {
        // 查询现有会话
        MessageConversation conversation = messageConversationMapper.selectOne(
                new LambdaQueryWrapper<MessageConversation>()
                        .eq(MessageConversation::getUserId, userId)
                        .eq(MessageConversation::getOtherUserId, otherUserId)
        );
        if (conversation == null) {
            // 创建新会话
            conversation = MessageConversation.builder()
                    .userId(userId)
                    .otherUserId(otherUserId)
                    .lastMessageId(messageId)
                    .unreadCount(incrementUnread ? 1 : 0)
                    .build();
            messageConversationMapper.insert(conversation);
        } else {
            // 更新现有会话
            conversation.setLastMessageId(messageId);
            if (incrementUnread) {
                conversation.setUnreadCount(conversation.getUnreadCount() + 1);
            }
            messageConversationMapper.updateById(conversation);
        }
    }

    /**
     * 处理离线消息
     *
     * @param message 离线消息对象
     */
    /*@RabbitListener(queues = RabbitMQConfig.PRIVATE_MESSAGE_QUEUE)
    public void handleOfflineMessage(PrivateMessage message,
                                     @Header("amqp_receivedRoutingKey") String routingKey) {
        // 只处理路由键为 "message.send" 的消息
        if (MESSAGE_SEND.equals(routingKey)) {
            // 用户已上线，推送消息
            if (isUserOnline(message.getReceiverId())) {
                simpMessagingTemplate.convertAndSendToUser(
                        message.getReceiverId().toString(),
                        "/queue/messages",
                        convertToDTO(message));

                // 更新消息状态为已送达
                message.setStatus(MessageStatus.DELIVERED.getValue());
                privateMessageMapper.updateById(message);
            }
        }
        // 用户仍然离线，消息保留在数据库，等待用户上线时处理
    }*/

    /**
     * 获取会话消息
     *
     * @param userId      用户ID
     * @param otherUserId 对方用户ID
     * @param page        分页页码
     * @param size        每页大小
     * @return 消息DTO列表
     */
    @Transactional
    public List<MessageDTO> getConversationMessages(Long userId, Long otherUserId, int page, int size) {
        // 分页参数
        Page<PrivateMessage> queryPage = new Page<>(page, size);
        // 构建查询条件
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.nested(w -> w // nested：构建嵌套查询条件的方法 将一组查询条件包裹在一个逻辑块中 通常为小括号()
                        .eq(PrivateMessage::getSenderId, userId)
                        .eq(PrivateMessage::getReceiverId, otherUserId)
                        .or()
                        .eq(PrivateMessage::getSenderId, otherUserId)
                        .eq(PrivateMessage::getReceiverId, userId))
                .ne(PrivateMessage::getStatus, MessageStatus.RECALLED.getValue()) // 状态不能是已撤回
                .orderByDesc(PrivateMessage::getCreateTime); // 按消息创建时间降序
        // 查询消息
        Page<PrivateMessage> messagePage = privateMessageMapper.selectPage(queryPage, wrapper);
        // 标记来自对方的消息为已读
        markMessagesAsRead(userId, otherUserId);
        // 返回DTO列表
        return messagePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 标记消息为已读
     *
     * @param userId   用户ID
     * @param senderId 发送者ID
     */
    @Transactional
    public void markMessagesAsRead(Long userId, Long senderId) {
        // 更新消息状态
        LambdaUpdateWrapper<PrivateMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PrivateMessage::getReceiverId, userId)
                .eq(PrivateMessage::getSenderId, senderId)
                .eq(PrivateMessage::getIsRead, false)
                .set(PrivateMessage::getIsRead, true)
                .set(PrivateMessage::getStatus, MessageStatus.READ.getValue());
        privateMessageMapper.update(null, updateWrapper);
        // 重置会话未读计数
        LambdaUpdateWrapper<MessageConversation> conversationUpdateWrapper = new LambdaUpdateWrapper<>();
        conversationUpdateWrapper.eq(MessageConversation::getUserId, userId)
                .eq(MessageConversation::getOtherUserId, senderId)
                .set(MessageConversation::getUnreadCount, 0);
        // 更新会话信息
        messageConversationMapper.update(null, conversationUpdateWrapper);
        // 通知发送者消息已读
        MessageReadNotificationDTO notification = new MessageReadNotificationDTO(userId, senderId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRIVATE_MESSAGE_EXCHANGE, // 交换机
                RabbitMQRoutingKeysConstant.MESSAGE_READ, // 路由键
                notification);
    }

    /**
     * 撤回消息
     *
     * @param userId    用户ID
     * @param messageId 消息ID
     * @return 是否撤回成功
     */
    @Transactional
    public boolean recallMessage(Long userId, Long messageId) {
        // 获取消息
        PrivateMessage message = privateMessageMapper.selectById(messageId);
        // 验证消息存在且属于当前用户
        if (message == null || !message.getSenderId().equals(userId)) {
            return false;
        }
        // 检查是否在可撤回时间范围内 (计算消息创建时间和当前时间差 判断是否超过了撤回的时间限制)
        long messageAge = ChronoUnit.SECONDS.between(message.getCreateTime(), LocalDateTime.now());
        if (messageAge > recallTimeLimit) {
            return false;
        }
        // 更新消息状态
        message.setStatus(MessageStatus.RECALLED.getValue());
        privateMessageMapper.updateById(message);
        // 通知接收者消息已撤回
        MessageRecallNotificationDTO notification = new MessageRecallNotificationDTO(messageId, userId, message.getReceiverId());
        // 如果接收者在线，直接通过WebSocket通知
        if (isUserOnline(message.getReceiverId())) {
            simpMessagingTemplate.convertAndSendToUser(
                    message.getReceiverId().toString(),
                    "/queue/messages.recall", // TODO WebSocket订阅的队列在前端中定义
                    notification);
        } else {
            // 接收者离线，通过RabbitMQ发送通知
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PRIVATE_MESSAGE_EXCHANGE,
                    RabbitMQRoutingKeysConstant.MESSAGE_RECALL,
                    notification);
        }
        return true;
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        LambdaQueryWrapper<UserConnection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConnection::getUserId, userId)
                .eq(UserConnection::getIsOnline, true);

        return userConnectionMapper.selectCount(wrapper) > 0;
    }

    /**
     * 推送离线消息
     * 当用户上线时，从 RabbitMQ 队列中取出所有未处理的离线消息并推送给用户
     *
     * @param userId 用户ID
     */
    public void pushOfflineMessages(Long userId) {
        // 查询所有未读消息
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivateMessage::getReceiverId, userId)
                .eq(PrivateMessage::getIsRead, false)
                .eq(PrivateMessage::getStatus, MessageStatus.SENT.getValue())
                .orderByAsc(PrivateMessage::getCreateTime); // 按发送时间升序

        List<PrivateMessage> offlineMessages = privateMessageMapper.selectList(wrapper);

        // 推送离线消息
        for (PrivateMessage message : offlineMessages) {
            simpMessagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/messages",
                    convertToDTO(message));

            // 更新消息状态为已送达
            message.setStatus(MessageStatus.DELIVERED.getValue());
            privateMessageMapper.updateById(message);
        }
    }

    /**
     * 转换为DTO
     *
     * @param message 私信消息对象
     * @return 消息DTO对象
     */
    MessageDTO convertToDTO(PrivateMessage message) {
        // 获取发送者信息
        SysUser sender = sysUserMapper.selectById(message.getSenderId());

        return MessageDTO.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .senderName(sender.getUserName())
                .senderAvatar(sender.getUserAvatar())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .status(message.getStatus())
                .isRead(message.getIsRead())
                .createTime(message.getCreateTime())
                .build();
    }

    /**
     * 获取未读消息数
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    public int getUnreadMessageCount(Long userId) {
        LambdaQueryWrapper<PrivateMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivateMessage::getReceiverId, userId)
                .eq(PrivateMessage::getIsRead, false);

        return privateMessageMapper.selectCount(wrapper).intValue();
    }

    /**
     * 获取会话列表
     *
     * @param userId 用户ID
     * @param page   分页页码
     * @param size   每页大小
     * @return 会话DTO分页对象
     */
    public Page<ConversationDTO> getUserConversations(Long userId, int page, int size) {
        // 分页参数
        Page<MessageConversation> queryPage = new Page<>(page, size);
        // 构建查询条件
        LambdaQueryWrapper<MessageConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageConversation::getUserId, userId)
                .orderByDesc(MessageConversation::getLastUpdateTime); // 按照最后更新时间降序
        // 查询会话
        Page<MessageConversation> conversationPage = messageConversationMapper.selectPage(queryPage, wrapper);
        // 转换为DTO
        List<ConversationDTO> conversationDTOs = conversationPage.getRecords().stream()
                .map(this::enrichConversation)
                .collect(Collectors.toList());
        // 构建结果
        Page<ConversationDTO> result = new Page<>();
        result.setRecords(conversationDTOs);
        result.setCurrent(conversationPage.getCurrent());
        result.setSize(conversationPage.getSize());
        result.setTotal(conversationPage.getTotal());
        return result;
    }

    /**
     * 丰富会话信息
     *
     * @param conversation 会话对象
     * @return 会话DTO对象
     */
    private ConversationDTO enrichConversation(MessageConversation conversation) {
        // 获取最后一条消息
        PrivateMessage lastMessage = privateMessageMapper.selectById(conversation.getLastMessageId());
        // 获取对方用户信息
        SysUser otherUser = sysUserMapper.selectById(conversation.getOtherUserId());
        // 检查对方是否在线
        boolean isOnline = isUserOnline(conversation.getOtherUserId());
        return ConversationDTO.builder()
                .conversationId(conversation.getConversationId())
                .otherUserId(conversation.getOtherUserId())
                .otherUserName(otherUser.getUserName())
                .otherUserAvatar(otherUser.getUserAvatar())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .messageType(lastMessage != null ? lastMessage.getMessageType().name() : null)
                .unreadCount(conversation.getUnreadCount())
                .lastUpdateTime(conversation.getLastUpdateTime())
                .isOnline(isOnline)
                .build();
    }
}