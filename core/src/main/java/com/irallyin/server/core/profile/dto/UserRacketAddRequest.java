package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRacketAddRequest {
    @NotBlank(message = "球拍不能为空")
    private String catalogId;
}
