package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileTennisLevelUpdateRequest {
    @NotNull(message = "网球水平不能为空")
    @DecimalMin(value = "0.5", message = "网球水平不能低于0.5")
    @DecimalMax(value = "7.0", message = "网球水平不能高于7.0")
    private Double ntrpRating;
}
