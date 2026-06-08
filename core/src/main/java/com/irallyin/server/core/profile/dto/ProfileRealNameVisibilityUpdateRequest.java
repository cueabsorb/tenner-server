package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileRealNameVisibilityUpdateRequest {
    @NotNull(message = "实名展示选择不能为空")
    private Boolean visible;
}
