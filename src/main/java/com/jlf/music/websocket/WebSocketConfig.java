package com.jlf.music.websocket;

import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * @Description WebSocket配置类 - 使用原生WebSocket
 * @Author JLF
 * @Date 2025/4/14 12:53
 * @Version 1.0
 */


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Value("${app.websocket.allowed-origins}")
    private String allowedOrigins;
    @Value("${app.websocket.endpoint}")
    private String endpoint;

    @Resource
    private CustomWebSocketHandler webSocketHandler;

    @Resource
    private AuthHandshakeInterceptor authInterceptor;

    /**
     * 注册WebSocket处理器和路径
     */
    @Override
    public void registerWebSocketHandlers(@NotNull WebSocketHandlerRegistry registry) {
        // 注册WebSocket处理器和路径
        // WebSocket处理器 - 负责处理WebSocket连接的生命周期事件和消息传递
        // 路径 - 客户端与服务器建立WebSocket连接的URL地址 用于区分不同的服务端点
        registry.addHandler(webSocketHandler, endpoint)
                .addInterceptors(authInterceptor)
                .setAllowedOrigins(allowedOrigins);
    }


    /**
     * 配置WebSocket参数
     *
     * @return ServletServerContainerFactoryBean
     */
    /*@Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 设置消息缓冲区大小
        container.setMaxTextMessageBufferSize(8192);
        // 设置二进制消息缓冲区大小
        container.setMaxBinaryMessageBufferSize(8192);
        // 设置会话空闲超时（毫秒）
        container.setMaxSessionIdleTimeout(1000 * 60 * 30L); // 30分钟
        return container;
    }*/
}
