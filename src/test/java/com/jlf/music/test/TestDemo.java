package com.jlf.music.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class TestDemo {
    @Test
    void test() {
        // 当天时间
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        System.out.println("date = " + date);
        // 前一天时间
        String processDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        System.out.println("processDate = " + processDate);
    }
}
