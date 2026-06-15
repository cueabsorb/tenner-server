package com.irallyin.server.core.oss.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OssSignedUrlRequest {
    @NotBlank(message = "OSS objectKey不能为空")
    private String objectKey;
}
