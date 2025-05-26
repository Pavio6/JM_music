package com.jlf.music;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @Author JLF
 * @Description 项目启动类
 * @Date 2025/03/16 21:49
 */
@Slf4j
@MapperScan("com.jlf.music.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true) // 启用 AOP 并暴露代理对象
@EnableScheduling // 启用定时任务
@EnableCaching // 开启缓存注解功能
public class JMMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(JMMusicApplication.class, args);
        log.info("====================================项目启动成功, SpringBoot Version：[{}]====================================",
                SpringApplication.class.getPackage().getImplementationVersion());
    }
}
