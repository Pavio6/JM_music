package com.jlf.music.socket;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jlf.music.common.enumerate.OnlineStatus;
import com.jlf.music.config.RabbitMQConfig;
import com.jlf.music.entity.UserConnection;
import com.jlf.music.mapper.UserConnectionMapper;
import com.jlf.music.socket.dto.UserStatusMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.jlf.music.common.constant.RabbitMQRoutingKeysConstant.STATUS_OFFLINE;
import static com.jlf.music.common.constant.RabbitMQRoutingKeysConstant.STATUS_ONLINE;

/**
 * 用户连接服务
 * 负责管理用户的连接状态，包括保存连接、更新状态、检查在线状态等
 */
@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final UserConnectionMapper userConnectionMapper;
    private final RabbitTemplate rabbitTemplate;
    private final PrivateMessageService privateMessageService;

    /**
     * 保存用户连接
     * 当用户建立连接时，保存连接信息并发送用户上线状态
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     */
    @Transactional
    public void saveConnection(Long userId, String sessionId) {
        // 生成连接ID
        String connectionId = generateConnectionId(userId, sessionId);
        // 构建用户连接对象
        UserConnection connection = UserConnection.builder()
                .connectionId(connectionId) // 连接ID
                .userId(userId) // 用户ID
                .sessionId(sessionId) // 会话ID
                .isOnline(OnlineStatus.ONLINE.getValue()) // 连接状态（在线）
                .connectTime(LocalDateTime.now()) // 连接时间
                .lastActiveTime(LocalDateTime.now()) // 最后活动时间
                .build();
        // 保存连接信息到数据库
        userConnectionMapper.insert(connection);
        // 发送用户上线状态到RabbitMQ - 异步操作
        UserStatusMessageDTO statusMessage = new UserStatusMessageDTO(userId, OnlineStatus.ONLINE);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_STATUS_EXCHANGE, // 交换机
                STATUS_ONLINE, // 路由键
                statusMessage // 消息内容
        );
        // 推送离线消息
        privateMessageService.pushOfflineMessages(userId);
    }

    /**
     * 移除用户连接
     * 当用户断开连接时，更新连接状态并发送用户下线通知
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     */
    @Transactional
    public void removeConnection(Long userId, String sessionId) {
        // 生成连接ID
        String connectionId = generateConnectionId(userId, sessionId);
        // 查询连接记录
        UserConnection connection = userConnectionMapper.selectOne(
                new LambdaQueryWrapper<UserConnection>()
                        .eq(UserConnection::getConnectionId, connectionId)
        );
        if (connection != null) {
            // 更新连接状态为离线
            connection.setIsOnline(OnlineStatus.OFFLINE.getValue());
            connection.setLastActiveTime(LocalDateTime.now());
            userConnectionMapper.updateById(connection);

            // 检查用户是否还有其他活跃连接
            Long activeConnectionCount = userConnectionMapper.selectCount(
                    new LambdaQueryWrapper<UserConnection>()
                            .eq(UserConnection::getUserId, userId)
                            .eq(UserConnection::getIsOnline, OnlineStatus.ONLINE.getValue())
            );
            // 如果没有其他活跃连接，则发送用户下线状态
            if (activeConnectionCount == 0) {
                // 发送用户下线状态到RabbitMQ
                UserStatusMessageDTO statusMessage = new UserStatusMessageDTO(userId, OnlineStatus.OFFLINE);
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.USER_STATUS_EXCHANGE, // 交换机
                        STATUS_OFFLINE, // 路由键
                        statusMessage // 消息内容
                );
            }
        }
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        // 构建查询条件
        LambdaQueryWrapper<UserConnection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConnection::getUserId, userId)
                .eq(UserConnection::getIsOnline, OnlineStatus.ONLINE.getValue());

        // 查询在线连接数量
        return userConnectionMapper.selectCount(wrapper) > 0;
    }

    /**
     * 获取用户活跃连接列表
     *
     * @param userId 用户ID
     * @return 用户的活跃连接列表
     */
    public List<UserConnection> getUserActiveConnections(Long userId) {
        // 构建查询条件
        LambdaQueryWrapper<UserConnection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserConnection::getUserId, userId)
                .eq(UserConnection::getIsOnline, OnlineStatus.ONLINE.getValue());

        // 查询活跃连接列表
        return userConnectionMapper.selectList(wrapper);
    }


    /**
     * 生成连接ID
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 连接ID
     */
    private String generateConnectionId(Long userId, String sessionId) {
        // 连接ID格式：  用户ID:会话ID
        return userId + ":" + sessionId;
    }
}