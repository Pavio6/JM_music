package com.jlf.music.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * TODO 用户私信模块暂未开发完
 * WebSocket核心配置类
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker // 启用WebSocket消息代理 允许处理WebSocket消息
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.websocket.endpoint}")
    private String endpoint;

    @Value("${app.websocket.allowed-origins}")
    private String allowedOrigins;

    // Spring WebSocket 自定义拦截器
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        // 手动注册 WebSocket 端点
        exporter.setAnnotatedEndpointClasses(WebSocketConfig.class);
        return exporter;
    }

    /**
     * 设置线程池任务调度器
     */
    @Bean
    public TaskScheduler webSocketMessageBrokerTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("websocket-heartbeat-thread-");
        scheduler.setDaemon(true);
        return scheduler;
    }

    /**
     * 注册WebSocket端点, 并设置允许的源、拦截器以及是否使用SockJS
     *
     * @param registry STOMP端点注册器，用于配置WebSocket端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("WebSocket endpoint: {}", endpoint);
        registry.addEndpoint(endpoint) // websocket端点
                .setAllowedOriginPatterns(allowedOrigins) // 设置允许的源
                .withSockJS(); // 启用SockJS支持
    }

    /**
     * 配置消息代理，定义消息的发送和接收规则
     * 该方法用于配置消息代理，包括启用简单的内存消息代理、设置应用目的地前缀和用户目的地前缀
     *
     * @param registry 消息代理注册器，用于配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 使用内存消息代理，并指定代理目的地前缀
        // topic前缀则表示广播主题，用于一对多消息广播
        // queue前缀表示这是一个点对点队列，通常用于一对一消息传递
        registry.enableSimpleBroker("/topic", "/queue")
                // 客户端发送心跳间隔 , 客户端等待服务器响应的最大时间
                .setHeartbeatValue(new long[]{10000, 20000})
                .setTaskScheduler(webSocketMessageBrokerTaskScheduler());
        // 设置客户端发送消息的前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 设置用户点对点消息的前缀
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 配置客户端入站通道的拦截器
     * 该方法用于在客户端入站通道中添加自定义的拦截器，以便在消息到达时进行拦截处理
     *
     * @param registration 通道注册器，用于配置客户端入站通道
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor); // 添加自定义通道拦截器
    }
}