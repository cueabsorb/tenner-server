package com.irallyin.server.core.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginResponse {
    private String token;
    private String tokenType;
    private long expiresIn;
    private String email;
}
