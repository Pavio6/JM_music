package com.jlf.music.socket;

import com.jlf.music.common.enumerate.OnlineStatus;
import com.jlf.music.security.LoginUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * @Author: JLF
 * @Description: WebSocket事件监听器，用于监听WebSocket连接和断开事件
 * @Date 2025/3/16 21:53
 */
@Component
@Slf4j
public class WebSocketEventListener {

    @Resource
    private ConnectionService connectionService;

    /**
     * 监听WebSocket连接事件
     * 当客户端成功连接到WebSocket时触发
     *
     * @param event WebSocket连接事件对象，包含连接相关信息
     */
    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            Long userId = getUserIdFromPrincipal(accessor.getUser());
            String sessionId = accessor.getSessionId();
            if (userId != null && sessionId != null) {
                // 保存用户连接
                connectionService.saveConnection(userId, sessionId);
            }
        }
    }

    /**
     * 监听WebSocket断开事件
     * 当客户端断开WebSocket连接时触发
     *
     * @param event WebSocket断开事件对象，包含断开相关信息
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() != null) {
            Long userId = getUserIdFromPrincipal(accessor.getUser());
            String sessionId = accessor.getSessionId();

            if (userId != null && sessionId != null) {
                // 移除用户连接
                connectionService.removeConnection(userId, sessionId);
                log.info("用户 {} 断开连接", userId);
            }
        }
    }

    /**
     * 从Principal中获取userId
     */
    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal instanceof Authentication) {
            Object user = ((Authentication) principal).getPrincipal();
            if (user instanceof LoginUser) {
                return ((LoginUser) user).getUser().getUserId();
            }
        }
        return null;
    }
}