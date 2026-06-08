package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HabitCourtAddRequest {
    @NotBlank(message = "球场ID不能为空")
    private String courtId;

    private Boolean isPrimary;
}
