package com.jlf.music.security;

import cn.hutool.json.JSONUtil;
import com.jlf.music.common.Result;
import com.jlf.music.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 认证异常处理器
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        if (authException instanceof UsernameNotFoundException) {
            log.info("捕捉到 UsernameNotFoundException 异常: {}", authException.getMessage());
        }
        String errorMessage = authException.getMessage();
        Result<Void> result = Result.fail(401, errorMessage);
        String jsonStr = JSONUtil.toJsonStr(result);
        // 处理异常
        WebUtils.renderString(response, jsonStr);
    }
}
