package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileGenderUpdateRequest {
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "male|female", message = "性别参数无效")
    private String gender;
}
