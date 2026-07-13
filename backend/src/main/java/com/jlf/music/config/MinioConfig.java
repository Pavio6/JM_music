package com.jlf.music.config;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Resource
    private MinioInfo minioInfo;
    // minio的客户端对象
    // MinioClient是单例的 并且线程安全的
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioInfo.getEndpoint()) // 访问minio的地址
                .credentials(minioInfo.getAccessKey(), minioInfo.getSecretKey()) // 账号和密码
                .build();
    }

}