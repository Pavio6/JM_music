package com.jlf.music.websocket;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlf.music.common.enumerate.MessageType;
import com.jlf.music.common.enumerate.OnlineStatus;
import com.jlf.music.entity.MessageConversation;
import com.jlf.music.entity.PrivateMessage;
import com.jlf.music.entity.UserConnection;
import com.jlf.music.mapper.MessageConversationMapper;
import com.jlf.music.mapper.UserConnectionMapper;
import com.jlf.music.service.PrivateMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.jlf.music.common.constant.WebSocketConstant.*;
import static com.jlf.music.websocket.AuthHandshakeInterceptor.USER_ID_KEY;

@Component
@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {
    @Resource
    private UserConnectionMapper userConnectionMapper;
    @Resource
    private PrivateMessageService messageService;

    // 用户ID与WebSocketSession的映射
    private static final ConcurrentMap<Long, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
    // 用户之间的活跃聊天映射 记录用户当前正在与哪个用户聊天
    private static final ConcurrentMap<Long, Long> activeChats = new ConcurrentHashMap<>();

    /**
     * 连接建立时触发
     * 将用户加入在线用户列表，并通知其上线状态。
     */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        Long userId = getUserId(session);
        log.info("userId: {} 已连接!", userId);

        onlineUsers.put(userId, session);
        // 查询是否有该用户连接信息
        UserConnection userConnectionInfo = userConnectionMapper.selectOne(new LambdaQueryWrapper<UserConnection>()
                .eq(UserConnection::getUserId, userId));
        if (userConnectionInfo != null) {
            // 更新用户的连接信息
            LambdaUpdateWrapper<UserConnection> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserConnection::getUserId, userId)
                    .set(UserConnection::getIsOnline, OnlineStatus.ONLINE.getValue())
                    .set(UserConnection::getConnectionId, UUID.randomUUID().toString())
                    .set(UserConnection::getConnectTime, LocalDateTime.now())
                    .set(UserConnection::getLastActiveTime, LocalDateTime.now())
                    .set(UserConnection::getSessionId, session.getId());
            userConnectionMapper.update(updateWrapper);
        } else {
            // 保存用户连接状态信息
            UserConnection userConnection = UserConnection.builder()
                    .connectionId(UUID.fastUUID().toString())
                    .userId(userId)
                    .connectTime(LocalDateTime.now())
                    .isOnline(OnlineStatus.ONLINE.getValue())
                    .lastActiveTime(LocalDateTime.now())
                    .sessionId(session.getId())
                    .build();
            userConnectionMapper.insert(userConnection);
        }
        // 通知其他用户状态的改变
//        notifyUserStatus(userId, true);
    }


    /**
     * 处理接收到的文本消息
     * 根据消息类型分发处理逻辑（聊天消息或撤回消息）
     */
    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        // 获取发送者id
        Long senderId = getUserId(session);
        // 将json字符串解析为JsonNode对象
        JsonNode json = new ObjectMapper().readTree(message.getPayload());
        if (TYPE_MESSAGE.equals(json.get("type").asText())) {
            // 处理发送消息
            handleChatMessage(senderId, json);
        } else if (TYPE_RECALL.equals(json.get("type").asText())) {
            // 处理撤回消息
            handleRecallMessage(senderId, json);
        } else if (TYPE_CHAT_ACTIVE.equals(json.get("type").asText())) {
            // 处理聊天窗口激活事件
            handleChatActive(senderId, json);
        } else if (TYPE_CHAT_INACTIVE.equals(json.get("type").asText())) {
            // 处理聊天窗口关闭事件
            handleChatInactive(senderId);
        } else if (TYPE_HEART_BEAT.equals(json.get("type").asText())) {
            // 处理心跳
            log.debug("Received heartbeat from user: {}", senderId);
            return;
        } else {
            // 记录未知消息类型得日志
            log.warn("Received unknown message type: {}", json.get("type").asText());
        }
    }

    /**
     * 处理聊天窗口激活事件，用户进入聊天页面
     */
    private void handleChatActive(Long userId, JsonNode json) {
        Long otherUserId = json.get("otherUserId").asLong();
        // 记录用户正在与谁聊天
        activeChats.put(userId, otherUserId);
        log.info("用户{}激活了与用户{}的聊天窗口", userId, otherUserId);
        log.info("此时的-activeChats: {}", activeChats);
    }

    /**
     * 处理聊天窗口关闭事件，用户离开聊天页面
     */
    private void handleChatInactive(Long userId) {
        // 移除用户的活跃聊天记录
        activeChats.remove(userId);
        log.info("关闭了用户{}的聊天窗口:", userId);
        log.info("关闭后的-activeChats: {}", activeChats);
    }

    /**
     * 检查用户是否正在与指定用户聊天
     */
    private boolean isUserActivelyChattingWith(Long userId, Long otherUserId) {
        Long activeChat = activeChats.get(userId);
        return activeChat != null && activeChat.equals(otherUserId);
    }

    /**
     * 处理聊天消息
     * 保存消息并发送给接收方和发送方
     */
    private void handleChatMessage(Long senderId, JsonNode json) {
        // 获取接收者id
        Long receiverId = json.get("to").asLong();
        // 获取内容
        String content = json.get("content").asText();
        // 获取内容类型
        MessageType type = MessageType.valueOf(json.get("messageType").asText());
        // 检查接收者是否在与发送者进行活跃聊天，如果是，则消息状态为已读
        boolean shouldMarkAsRead = isUserActivelyChattingWith(receiverId, senderId);
        // 保存消息
        PrivateMessage privateMessage = messageService.saveMessage(senderId, receiverId, content, type, shouldMarkAsRead);
        // 发送消息给双方
//        sendMessageToUser(senderId, buildMessageJson(senderId, receiverId, content, type, privateMessage.getMessageId()));
//        log.info("用户 {} 发送了一条消息", senderId);
//        log.info("服务器推送消息给发送者: {}", senderId);
        sendMessageToUser(receiverId, buildMessageJson(senderId, receiverId, content, type, privateMessage.getMessageId()));
        log.info("服务器推送消息给接收者: {}", receiverId);
    }

    /**
     * 处理撤回消息
     * 撤回指定消息并通知双方
     */
    private void handleRecallMessage(Long senderId, JsonNode json) {
        Long messageId = json.get("messageId").asLong();
        PrivateMessage message = messageService.getById(messageId);
        if (message == null) {
            return;
        } else {
            // 获取当前时间和消息创建时间
            LocalDateTime now = LocalDateTime.now();
            log.info("当前时间: {}", now);
            LocalDateTime createTime = message.getCreateTime();
            log.info("发送消息的时间: {}", createTime);
            // 计算时间差
            Duration duration = Duration.between(createTime, now);
            long diffMinutes = duration.toMinutes();
            if (diffMinutes > 2) {
                sendError(senderId, message.getReceiverId(), RECALL_MESSAGE_TIMEOUT);
                return;
            }
        }
        // 调用撤回消息的方法
        Boolean isSuccess = messageService.recallMessage(messageId, senderId);
        if (isSuccess) {
            // 通知双方撤回
            sendMessageToUser(message.getSenderId(), buildRecallJson(message.getSenderId(), message.getReceiverId(), messageId));
            log.info("服务器向发送者发送撤回消息: {}", message.getSenderId());
            sendMessageToUser(message.getReceiverId(), buildRecallJson(message.getSenderId(), message.getReceiverId(), messageId));
            log.info("服务器向接收者发送撤回消息: {}", message.getReceiverId());
        }

    }

    /**
     * 发送错误信息给客户端
     */
    private void sendError(@NotNull Long senderId, @NotNull Long receiverId, @NotNull String errorMessage) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("type", "recall_error");
        errorResponse.put("message", errorMessage);
        errorResponse.put("sender", senderId);
        errorResponse.put("to", receiverId);
        try {
            String jsonResponse = mapper.writeValueAsString(errorResponse);
            sendMessageToUser(senderId, jsonResponse);
        } catch (JsonProcessingException e) {
            log.error("构建错误响应失败", e);
        }
    }

    /**
     * 通知其他用户的在线状态变化 -> 前端根据用户是否在线 决定用户头像 亮起/灰色
     */
    private void notifyUserStatus(Long userId, boolean isOnline) {
        String statusJson = String.format("{\"type\":\"status\",\"userId\":%d,\"online\":%b}", userId, isOnline);

        // 通知所有在线用户
        onlineUsers.keySet().forEach(uid -> {
            if (!uid.equals(userId)) {
                sendMessageToUser(uid, statusJson);
            }
        });
    }

    /**
     * 连接关闭时触发。
     * 将用户从在线用户列表移除，并通知其离线状态。
     */
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        Long userId = getUserId(session);
        onlineUsers.remove(userId);
        // 修改用户在线状态和最后活动时间
        LambdaUpdateWrapper<UserConnection> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserConnection::getUserId, userId)
                .set(UserConnection::getIsOnline, OnlineStatus.OFFLINE.getValue())
                .set(UserConnection::getLastActiveTime, LocalDateTime.now());
        userConnectionMapper.update(updateWrapper);
        log.info("用户 {} 断开连接", userId);
//        notifyUserStatus(userId, false);
    }

    /**
     * 构建聊天消息的JSON字符串。
     */
    private String buildMessageJson(Long senderId, Long receiverId, String content, MessageType type, Long messageId) {
        return String.format("{\"type\":\"message\",\"sender\":%d,\"to\":%d,\"content\":\"%s\",\"messageType\":\"%s\",\"messageId\":\"%d\"}",
                senderId, receiverId, content, type.name(), messageId);
    }

    /**
     * 构建撤回消息的JSON字符串。
     */
    private String buildRecallJson(Long senderId, Long receiverId, Long messageId) {
        return String.format("{\"type\":\"recall\",\"sender\":%d,\"to\":%d,\"messageId\":%d}",
                senderId, receiverId, messageId);
    }

    /**
     * 从会话属性中获取用户ID。
     */
    private Long getUserId(WebSocketSession session) {
        return (Long) session.getAttributes().get(USER_ID_KEY);
    }

    /**
     * 向指定用户发送消息。
     */
    public void sendMessageToUser(Long userId, String message) {
        WebSocketSession session = onlineUsers.get(userId);
        if (session != null && session.isOpen()) {
            try {
                // 通过会话发送消息
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("消息发送失败", e);
            }
        }
    }
}
