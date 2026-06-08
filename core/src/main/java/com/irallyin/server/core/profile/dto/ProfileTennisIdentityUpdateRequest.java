package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileTennisIdentityUpdateRequest {
    @NotBlank(message = "网球身份不能为空")
    @Pattern(regexp = "amateur|coach|professional", message = "网球身份参数无效")
    private String tennisIdentity;
}
