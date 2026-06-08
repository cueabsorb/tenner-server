package com.irallyin.server.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendVerificationCodeRequest {
    @NotBlank
    private String identifier;

    @NotBlank
    @Pattern(regexp = "register|login", message = "scene must be register or login")
    private String scene;
}
