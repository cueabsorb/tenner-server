package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileIntroUpdateRequest {
    @Size(max = 160, message = "简介最多160个字符")
    private String intro;
}
