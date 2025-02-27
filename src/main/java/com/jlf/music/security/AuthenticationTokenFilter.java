package com.jlf.music.security;

import com.jlf.music.entity.SysUser;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jlf.music.common.constant.RedisConstant.USER_LOGIN_KEY;

/**
 * 自定义token验证过滤器
 */
@Slf4j
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }
        String key = USER_LOGIN_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        if (userMap.isEmpty()) {
            log.warn("用户未登录或已过期");
            throw new InsufficientAuthenticationException("用户未登录或已过期");
        }
        // 4. 构建用户权限信息
        List<GrantedAuthority> authorities = Arrays.stream(
                        ((String) userMap.get("authorities")).split(",")
                )
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 5. 封装用户信息到 LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(new SysUser(
                Long.parseLong((String) userMap.get("userId")),
                (String) userMap.get("userName"),
                (String) userMap.get("userAvatar")
        ));
        loginUser.setAuthorities(authorities);
        // 6. 创建 Authentication 对象并存入 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. 刷新 Token 有效期（滑动过期）
        stringRedisTemplate.expire(key, 300000L, TimeUnit.MINUTES);
        // 8. 放行请求
        filterChain.doFilter(request, response);
    }
}
