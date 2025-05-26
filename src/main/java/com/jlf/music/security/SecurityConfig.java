package com.jlf.music.security;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //配置类
@EnableWebSecurity
@EnableMethodSecurity // 启用方法级安全功能
public class SecurityConfig {
    /**
     * 自定义身份认证过滤器
     */
    @Resource
    private AuthenticationTokenFilter authenticationTokenFilter;
    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Resource
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 暴露 AuthenticationManager Bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置Spring Security的过滤链。
     *
     * @param http 用于构建安全配置的HttpSecurity对象。
     * @return 返回配置好的SecurityFilterChain对象。
     * @throws Exception 如果配置过程中发生错误，则抛出异常。
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                // 将AuthenticationTokenFilter过滤器添加到UsernamePasswordAuthenticationFilter之前
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // anonymous() 严格限制只有匿名用户可以访问指定路径，已认证用户无法访问
                        // permitAll() 允许所有用户（包括匿名用户和已认证用户）访问指定路径。
                        .requestMatchers("/api/user/login", "/api/user/register").anonymous()
                        // 允许WebSocket端点访问
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/common/captcha/get").permitAll()
                        // 首页获取歌手、歌单、轮播图的接口 无需认证
                        .requestMatchers("/api/user/singer/page", "/api/user/playlist/page", "/api/user/carousel/list").permitAll()
                        // 管理员专属接口
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 用户和管理员共享接口
                        .requestMatchers("/api/user/**", "/common/**").hasAnyRole("ADMIN", "USER")

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated())
                // 设置会话创建策略为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 处理未认证
                        .accessDeniedHandler(customAccessDeniedHandler)            // 处理权限不足
                )
                // 开启跨域访问
                .cors();
        // 构建并返回安全过滤链
        return http.build();
    }

}