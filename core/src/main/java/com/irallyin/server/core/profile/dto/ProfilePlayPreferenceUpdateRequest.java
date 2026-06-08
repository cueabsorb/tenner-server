package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfilePlayPreferenceUpdateRequest {
    @NotBlank(message = "打球偏好不能为空")
    @Pattern(regexp = "singles|doubles|both", message = "打球偏好参数无效")
    private String playPreference;
}
