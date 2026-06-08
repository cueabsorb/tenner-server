package com.irallyin.server.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank
    private String authorizationCode;

    @NotBlank
    private String redirectUri;

    @NotBlank
    private String codeVerifier;
    private String deviceId;
    private String deviceInfo;
}
