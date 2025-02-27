package com.jlf.music.utils;

import com.jlf.music.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全上下文工具类，用于获取当前用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前用户的 Authentication 对象
     *
     * @return Authentication 对象
     * @throws IllegalStateException 如果用户未登录
     */
    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("用户未登录");
        }
        return authentication;
    }

    /**
     * 获取当前用户的 LoginUser 对象
     *
     * @return LoginUser 对象
     * @throws IllegalStateException 如果用户未登录或类型不匹配
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        throw new IllegalStateException("用户信息类型不匹配");
    }

    /**
     * 获取当前用户的 userId
     *
     * @return 用户ID
     * @throws IllegalStateException 如果用户未登录或类型不匹配
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser.getUser().getUserId();
    }


}