package com.jlf.music.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlf.music.common.enumerate.MessageType;
import com.jlf.music.entity.PrivateMessage;
import com.jlf.music.exception.ServiceException;
import com.jlf.music.service.FileService;
import com.jlf.music.service.PrivateMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.jlf.music.websocket.AuthHandshakeInterceptor.USER_ID_KEY;

@Component
@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private PrivateMessageService messageService;
    @Resource
    private FileService fileService;

    // 用户ID与WebSocketSession的映射
    private static final ConcurrentMap<Long, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();


    /**
     * 连接建立时触发
     * 将用户加入在线用户列表，并通知其上线状态。
     */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        Long userId = getUserId(session);
        onlineUsers.put(userId, session);
        // 通知其他用户状态的改变
        notifyUserStatus(userId, true);
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

        if ("message".equals(json.get("type").asText())) {
            // 处理发送消息
            handleChatMessage(senderId, json);
        } else if ("recall".equals(json.get("type").asText())) {
            // 处理撤回消息
            handleRecallMessage(senderId, json);
        } else {
            // 记录未知消息类型得日志
            log.warn("Received unknown message type: {}", json.get("type").asText());
        }
    }

    /**
     * 处理聊天消息
     * 保存消息并发送给接收方和发送方
     */
    private void handleChatMessage(Long senderId, JsonNode json) {
        // 获取接收者id
        Long receiverId = json.get("to").asLong();
        // 解析消息类型
//        String messageType = json.get("messageType").asText();
        // 获取内容
        String content = json.get("content").asText();

        // 获取内容类型
        MessageType type = MessageType.valueOf(json.get("messageType").asText());
        // 保存消息
        PrivateMessage privateMessage = messageService.saveMessage(senderId, receiverId, content, type);
        // 发送消息给双方
        sendMessageToUser(senderId, buildMessageJson(senderId, content, type, privateMessage.getMessageId()));
        sendMessageToUser(receiverId, buildMessageJson(senderId, content, type, privateMessage.getMessageId()));
    }

    /**
     * 处理撤回消息
     * 撤回指定消息并通知双方
     */
    private void handleRecallMessage(Long senderId, JsonNode json) {
        Long messageId = json.get("messageId").asLong();
        PrivateMessage message = messageService.getById(messageId);
        if (message == null) {
            throw new ServiceException("消息不存在");
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
                throw new ServiceException("消息已超过2分钟，无法撤回");
            }
        }
        // 调用撤回消息的方法
        Boolean isSuccess = messageService.recallMessage(messageId, senderId);
        if (isSuccess) {
            // 通知双方撤回
            sendMessageToUser(message.getSenderId(), buildRecallJson(messageId));
            sendMessageToUser(message.getReceiverId(), buildRecallJson(messageId));
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
        notifyUserStatus(userId, false);
    }

    /**
     * 构建聊天消息的JSON字符串。
     */
    private String buildMessageJson(Long senderId, String content, MessageType type, Long messageId) {
        return String.format("{\"type\":\"message\",\"sender\":%d,\"content\":\"%s\",\"messageType\":\"%s\",\"messageId\":\"%d\"}",
                senderId, content, type.name(), messageId);
    }

    /**
     * 构建撤回消息的JSON字符串。
     */
    private String buildRecallJson(Long messageId) {
        return String.format("{\"type\":\"recall\",\"messageId\":%d}", messageId);
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
