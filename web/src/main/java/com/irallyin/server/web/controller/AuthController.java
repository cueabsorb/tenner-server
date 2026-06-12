package com.irallyin.server.web.controller;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.auth.dto.AuthTokenResponse;
import com.irallyin.server.core.auth.dto.GoogleLoginRequest;
import com.irallyin.server.core.auth.dto.RefreshTokenRequest;
import com.irallyin.server.core.auth.dto.SendVerificationCodeRequest;
import com.irallyin.server.core.auth.dto.UserProfileResponse;
import com.irallyin.server.core.auth.dto.VerificationCodeResponse;
import com.irallyin.server.core.auth.dto.VerifyCodeRequest;
import com.irallyin.server.core.auth.service.AuthService;
import com.irallyin.server.core.auth.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "认证与登录")
public class AuthController {
    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;

    @PostMapping("/login/google")
    @Operation(summary = "Google OAuth 登录", description = "iOS ASWebAuthenticationSession 获取授权码后调用")
    public ApiResponse<AuthTokenResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletRequest servletRequest
    ) {
        try {
            if (request.getDeviceInfo() == null) {
                request.setDeviceInfo(servletRequest.getHeader("User-Agent"));
            }
            return ApiResponse.success(authService.loginWithGoogle(request, resolveClientIp(servletRequest)));
        } catch (Exception e) {
            log.error("Google login failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 App 登录态", description = "App 下次打开时用 Keychain 中的 refreshToken 换新 accessToken")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }

    @PostMapping("/verification-code")
    @Operation(summary = "发送验证码", description = "注册或登录场景发送 6 位验证码，Redis 中 5 分钟有效")
    public ApiResponse<VerificationCodeResponse> sendVerificationCode(
            @Valid @RequestBody SendVerificationCodeRequest request
    ) {
        return ApiResponse.success(verificationCodeService.sendCode(request));
    }

    @PostMapping("/verification-code/verify")
    @Operation(summary = "校验验证码", description = "校验 register/login 场景验证码，校验成功后删除 Redis 中的验证码")
    public ApiResponse<Void> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        verificationCodeService.verifyCode(request);
        return ApiResponse.success();
    }

    @PostMapping("/me")
    @Operation(summary = "获取当前用户", description = "用 Bearer accessToken 校验当前登录状态")
    public ApiResponse<UserProfileResponse> me(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ApiResponse.success(authService.getCurrentUser(userId.toString()));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
