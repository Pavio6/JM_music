package com.jlf.music;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

@SpringBootTest
public class PasswordEncoderTest {
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 密码编码器
     */
    @Test
    void testPasswordEncoder() {
        // 输入password的值
        String password = "";
        // 编码后的密码值
        String encode = passwordEncoder.encode(password);
        System.out.println("encode = " + encode);
        boolean matches = passwordEncoder.matches(encode, password);
        System.out.println("matches = " + matches);
    }

}
