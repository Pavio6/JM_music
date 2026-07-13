package com.jlf.music;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @Description 日期测试类
 * @Author JLF
 * @Date 2025/4/4 21:32
 * @Version 1.0
 */
@SpringBootTest
public class DataTest {
    @Test
    public void test01() {
        // 今天日期
        Date todayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        // 明天日期
        Date todayEnd = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println("todayStart = " + todayStart);
        System.out.println("todayEnd = " + todayEnd);
    }
}
