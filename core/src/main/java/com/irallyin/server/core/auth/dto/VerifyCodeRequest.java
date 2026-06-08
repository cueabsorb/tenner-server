package com.irallyin.server.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyCodeRequest {
    @NotBlank
    private String identifier;

    @NotBlank
    @Pattern(regexp = "register|login", message = "scene must be register or login")
    private String scene;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "code must be 6 digits")
    private String code;
}
