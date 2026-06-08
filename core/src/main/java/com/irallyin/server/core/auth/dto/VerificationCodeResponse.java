package com.irallyin.server.core.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationCodeResponse {
    private String identifier;
    private String scene;
    private long expiresInSeconds;
}
