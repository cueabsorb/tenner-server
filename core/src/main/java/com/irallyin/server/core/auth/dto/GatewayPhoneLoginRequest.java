package com.irallyin.server.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GatewayPhoneLoginRequest {
    @NotBlank(message = "网关认证Token不能为空")
    private String accessToken;
    private String carrier;
    private String deviceId;
    private String deviceInfo;
}
