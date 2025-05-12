package com.jlf.music.websocket;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jlf.music.common.constant.RedisConstant.USER_LOGIN_KEY;

/**
 * 自定义握手拦截器
 */
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 会话属性中存储用户ID的键
     */
    public static final String USER_ID_KEY = "USER_ID";

    /**
     * 会话属性中存储会话ID的键
     */
    public static final String SESSION_ID_KEY = "SESSION_ID";

    /**
     * WebSocket握手前的拦截逻辑
     * 验证用户身份并封装用户信息到WebSocket会话属性中
     */
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                   @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler,
                                   @NotNull Map<String, Object> attributes) {
        try {
            // 提取token
            String token = extractToken(request);
            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpSession session = servletRequest.getServletRequest().getSession();
                String key = USER_LOGIN_KEY + token;
                Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
                if (userMap.isEmpty()) {
                    throw new InsufficientAuthenticationException("用户未登录或已过期");
                }
                // 封装用户信息到WebSocket会话属性
                long userId = Long.parseLong((String) userMap.get("userId"));
                attributes.put(USER_ID_KEY, userId);
                attributes.put(SESSION_ID_KEY, session.getId());
                // 刷新Token有效期
                stringRedisTemplate.expire(key, 300000L, TimeUnit.MINUTES);
                return true;
            }
            // 请求不是ServletServerHttpRequest 则拒绝握手
            return false;
        } catch (Exception e) {
            // 设置未授权状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    /**
     * 从请求中提取Token
     * 优先从URL参数中获取
     */
    private String extractToken(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
            return req.getParameter("token");
        }
        return null;
    }

    /**
     * WebSocket握手完成后的处理逻辑
     * 当前无额外操作
     */
    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request,
                               @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler,
                               Exception exception) {
        // 握手完成后无需额外处理
    }
}
