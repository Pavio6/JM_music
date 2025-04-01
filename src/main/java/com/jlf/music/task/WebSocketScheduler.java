package com.jlf.music.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jlf.music.common.enumerate.OnlineStatus;
import com.jlf.music.entity.UserConnection;
import com.jlf.music.mapper.UserConnectionMapper;
import com.jlf.music.socket.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WebSocket相关方法定时器
 *
 * @Date 2025/3/23 16:56
 * @Author JLF
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketScheduler {
    private final UserConnectionMapper userConnectionMapper;
    private final ConnectionService connectionService;

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkStaleConnections() {
        // 获取超过心跳超时时间（例如2分钟）的活跃连接
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(2);

        List<UserConnection> staleConnections = userConnectionMapper.selectList(
                new LambdaQueryWrapper<UserConnection>()
                        .eq(UserConnection::getIsOnline, OnlineStatus.ONLINE.getValue())
                        .lt(UserConnection::getLastActiveTime, timeoutThreshold)
        );

        // 处理过期连接
        for (UserConnection conn : staleConnections) {
            connectionService.removeConnection(conn.getUserId(), conn.getSessionId());
            log.info("用户 {} 的连接 {} 因心跳超时而关闭", conn.getUserId(), conn.getSessionId());
        }
    }
}
