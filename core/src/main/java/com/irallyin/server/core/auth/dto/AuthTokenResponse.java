package com.irallyin.server.core.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserProfileResponse user;
}
