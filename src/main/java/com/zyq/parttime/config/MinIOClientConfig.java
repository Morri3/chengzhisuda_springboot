package com.zyq.parttime.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Data
//@Component
//public class MinIOClientConfig {
//    @Value("${minio.endpoint}")
//    private String endpoint;
//    @Value("${minio.accessKey}")
//    private String accessKey;
//    @Value("${minio.secretKey}")
//    private String secretKey;
//
//    /**
//     * 注入minio 客户端
//     *
//     * @return
//     */
//    @Bean
//    public MinioClient minioClient() {
//        return MinioClient.builder()
//                .endpoint(endpoint)
//                .credentials(accessKey, secretKey)
//                .build();
//    }
//}

@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinIOClientConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;// 桶名
    private String urlPrefix;// 统一前缀

    //创建minio连接对象
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}