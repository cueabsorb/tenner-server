package com.irallyin.server.core.oss;

import com.irallyin.server.core.oss.dto.OssSignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OssSignedUrlService {
    private final AliyunOssProperties properties;
    private final AliyunOssService ossService;

    public OssSignedUrlResponse generateReadUrl(String objectKey, String userId) {
        Duration expiresIn = Duration.ofHours(properties.getSignedUrlExpireHours());
        Instant expiresAt = Instant.now().plus(expiresIn);
        return OssSignedUrlResponse.builder()
                .objectKey(objectKey)
                .signedUrl(ossService.generateSignedUrl(objectKey, expiresIn))
                .expiresInSeconds(expiresIn.toSeconds())
                .expiresAt(expiresAt.toString())
                .build();
    }
}
