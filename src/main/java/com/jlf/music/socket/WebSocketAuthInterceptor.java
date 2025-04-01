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
            String token = accessor.getFirstNativeHeader("token");
            if (token != null & !Objects.requireNonNull(token).isEmpty()) {
                try {
                    String key = USER_LOGIN_KEY + token;
                    Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
                    if (userMap.isEmpty()) {
                        log.warn("用户未登录或已过期");
                        throw new InsufficientAuthenticationException("用户未登录或已过期");
                    }
                    List<GrantedAuthority> authorities = Arrays.stream(
                                    ((String) userMap.get("authorities")).split(",")
                            )
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // 封装用户信息到 LoginUser
                    LoginUser loginUser = new LoginUser();
                    loginUser.setUser(new SysUser(
                            Long.parseLong((String) userMap.get("userId")),
                            (String) userMap.get("userName"),
                            (String) userMap.get("userAvatar")
                    ));
                    loginUser.setAuthorities(authorities);
                    // 创建 Authentication 对象并存入 SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
                    // 设置用户认证信息到WebSocket会话
                    accessor.setUser(authentication);
                } catch (Exception e) {
                    log.warn("token验证失败, error: {}", e.getMessage());
                }
            }
        }
        return message;
    }
}
