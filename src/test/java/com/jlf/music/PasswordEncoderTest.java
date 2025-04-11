package com.jlf.music;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 根据原始密码生成加密后的
     */
    @Test
    void testPasswordEncoder() {
        // 输入password的值
        String password = "jlf111111";
        // 编码后的密码值
        String encode = passwordEncoder.encode(password);
        System.out.println("encode = " + encode);
        // 参数：原始密码 加密后密码
        boolean matches = passwordEncoder.matches(password, encode);
        System.out.println("matches = " + matches);
    }


}
