package com.jlf.music;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Test
    void testPasswordEncoder() {

        /**
         * 管理员的密码
         * jjj111111
         */
        String encode = passwordEncoder.encode("jjj111111");
        System.out.println("encode = " + encode);
        System.out.println("encode = " + encode);
        System.out.println("encode = " + encode);
        boolean matches = passwordEncoder.matches(encode, "jjj111111");
        System.out.println("matches = " + matches);
    }

}
