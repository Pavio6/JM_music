package com.jlf.music;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description
 * @Author JLF
 * @Date 2025/5/8 21:03
 * @Version 1.0
 */
@SpringBootTest
public class SensitiveWordTest {
    /**
     * 测试是否包含敏感词
     */
    @Test
    void test01() {
        final String text = "五星红旗迎风飘扬，毛主席的画像屹立在天安门前。";
        if (SensitiveWordHelper.contains(text)) {
            System.out.println(SensitiveWordHelper.findFirst(text));
        }
    }
}
