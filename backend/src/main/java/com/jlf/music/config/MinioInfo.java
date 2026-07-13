package com.jlf.music.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
// 将外部配置绑定到 Java 对象
@ConfigurationProperties(prefix = "minio")
public class MinioInfo {
    private String endpoint;
    private String accessKey;
    private String secretKey;

}
