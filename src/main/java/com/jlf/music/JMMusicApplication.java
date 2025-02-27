package com.jlf.music;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@MapperScan("com.jlf.music.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true) // 启用 AOP 并暴露代理对象
@EnableScheduling // 启用定时任务
public class JMMusicApplication {
    public static void main(String[] args) {
        SpringApplication.run(JMMusicApplication.class, args);
        log.info("====================================项目启动成功, SpringBoot Version：[{}]====================================",
                SpringApplication.class.getPackage().getImplementationVersion());
    }
}
