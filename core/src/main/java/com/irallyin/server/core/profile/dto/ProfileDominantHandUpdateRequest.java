package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileDominantHandUpdateRequest {
    @NotBlank(message = "常用手不能为空")
    @Pattern(regexp = "left|right", message = "常用手参数无效")
    private String dominantHand;
}
