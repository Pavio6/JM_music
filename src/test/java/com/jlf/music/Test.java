package com.jlf.music;

import cn.hutool.core.lang.UUID;

import java.time.LocalDate;

public class Test {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        String string = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        System.out.println("string = " + string);
        System.out.println("string = " + string);
        System.out.println("string = " + string);
        String s = java.util.UUID.randomUUID().toString();
        System.out.println("s = " + s);
        System.out.println("s = " + s);
        System.out.println("s = " + s);
    }

    @org.junit.jupiter.api.Test
    public void test1() {
        double b = 170.111;
        double bb = 170.999;
        System.out.println("bb = " + (int) bb);
        System.out.println("b = " + (int) b);
    }
}
