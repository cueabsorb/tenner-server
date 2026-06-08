package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileNameUpdateRequest {
    @NotBlank(message = "名字不能为空")
    @Size(min = 2, max = 24, message = "名字需为2-24个字符")
    @Pattern(regexp = "^[^!*&@<>/]+$", message = "名字包含无效字符")
    private String name;
}
