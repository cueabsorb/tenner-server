package com.irallyin.server.core.oss.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OssSignedUrlResponse {
    String objectKey;
    String signedUrl;
    Long expiresInSeconds;
    String expiresAt;
}
