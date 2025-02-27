package com.jlf.music.test;

import org.junit.jupiter.api.Test;

import java.util.Random;

public class RandomTest {
    @Test
    void testRandom() {
        Random random = new Random();
        System.out.println(random.nextInt(1));
    }
}
