package com.irallyin.server.core.oss;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OssUploadResult {
    String bucketName;
    String objectKey;
    String url;
    String contentType;
    Long contentLength;
    String etag;
}
