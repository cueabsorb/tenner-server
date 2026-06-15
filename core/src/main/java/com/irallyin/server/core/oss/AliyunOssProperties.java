package com.irallyin.server.core.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "irallyin.oss")
public class AliyunOssProperties {
    private boolean enabled = false;
    private String endpoint;
    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private String publicBaseUrl;
    private String objectKeyPrefix = "uploads";
    private List<String> allowedReadPrefixes = List.of("uploads", "image");
    private String imageDir = "images";
    private String videoDir = "videos";
    private long imageMaxSizeMb = 20;
    private long videoMaxSizeMb = 500;
    private long signedUrlExpireHours = 24;
}
