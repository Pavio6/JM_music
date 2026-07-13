package com.jlf.music.controller.web;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author JLF
 * @Date 2025/4/11 14:39
 * @Version 1.0
 */
@Slf4j
@RestController
public class TestController {
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/test-password")
    public String testPasswordMatch(@RequestParam String rawPassword, @RequestParam String encodedPassword) {

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        log.info("匹配结果：{}", matches);
        return "密码匹配结果: " + matches;
    }
}
