package com.jlf.music.socket;

import com.jlf.music.entity.SysUser;
import com.jlf.music.security.LoginUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.RedisConstant.USER_LOGIN_KEY;

/**
 * 自定义通道拦截器 用来进行认证
 * 实现ChannelInterceptor接口，可在消息发送到消息通道前进行拦截处理
 */
@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 在消息发送前进行预处理的方法。
     * 该方法会在消息发送到消息通道之前被调用，用于对消息进行自定义处理。
     *
     * @param message 待发送的消息对象，包含消息头和消息体。
     * @param channel 消息将要发送到的目标通道。
     * @return 返回处理后的消息对象。如果不需要修改消息，可以直接返回原始消息。
     */
    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel)
            throws MessagingException {
        // 将消息头包装为StompHeaderAccessor对象，方便操作STOMP协议相关头部信息
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // 检查是否为STOMP协议的CONNECT命令（客户端首次连接请求）
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 读取headers中的token的值
            String token = accessor.getFirstNativeHeader("token");
            log.info("收到CONNECT请求，token：{}", token);
            if (token != null & !Objects.requireNonNull(token).isEmpty()) {
                try {
                    // 调用封装的方法获取认证信息
                    UsernamePasswordAuthenticationToken authentication = getAuthenticationFromToken(token);
                    // 设置用户认证信息到WebSocket会话
                    accessor.setUser(authentication);
                } catch (Exception e) {
                    log.warn("token验证失败, error: {}", e.getMessage());
                }
            }
        }
        return message;
    }

    /**
     * 根据 Token 构建用户认证信息
     *
     * @param token 前端传递的认证令牌
     * @return 包含用户信息和权限的认证对象
     * @throws InsufficientAuthenticationException 如果出现以下情况：
     *                                             1. Token 对应的 Redis 数据不存在（用户未登录或已过期）
     *                                             2. 权限数据格式非法
     * @throws NumberFormatException               如果 userId 无法转换为 Long 类型
     */
    private UsernamePasswordAuthenticationToken getAuthenticationFromToken(String token) {
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(USER_LOGIN_KEY + token);
        if (userMap.isEmpty()) {
            throw new InsufficientAuthenticationException("用户未登录或 Token 已过期");
        }
        LoginUser loginUser = buildLoginUserFromRedis(userMap);
        return new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
    }

    /**
     * 从 Redis 数据构建登录用户信息
     *
     * @param userMap Redis 中存储的用户哈希数据
     * @return 封装后的 LoginUser 对象
     * @throws IllegalArgumentException 如果权限数据(authorities)不存在或格式错误
     */
    private LoginUser buildLoginUserFromRedis(Map<Object, Object> userMap) {
        SysUser sysUser = new SysUser(
                Long.parseLong((String) userMap.get("userId")),
                (String) userMap.get("userName"),
                (String) userMap.get("userAvatar")
        );

        List<GrantedAuthority> authorities = Arrays.stream(
                ((String) userMap.get("authorities")).split(",")
        ).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(sysUser);
        loginUser.setAuthorities(authorities);
        return loginUser;
    }
}
