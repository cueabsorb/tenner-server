package com.irallyin.server.appgateway.controller;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.auth.dto.AuthTokenResponse;
import com.irallyin.server.core.auth.dto.RefreshTokenRequest;
import com.irallyin.server.core.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@RequiredArgsConstructor
@Tag(name = "App Auth", description = "APP 登录态刷新接口")
public class AppAuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    @Operation(summary = "刷新 App 登录态", description = "用 Keychain 中的 refreshToken 换新 accessToken")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }
}
