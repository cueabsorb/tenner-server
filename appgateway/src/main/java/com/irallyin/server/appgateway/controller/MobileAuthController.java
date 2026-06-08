package com.irallyin.server.appgateway.controller;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.auth.dto.AuthTokenResponse;
import com.irallyin.server.core.auth.dto.EmailRegisterRequest;
import com.irallyin.server.core.auth.dto.EmailSendCodeRequest;
import com.irallyin.server.core.auth.dto.VerificationCodeResponse;
import com.irallyin.server.core.auth.service.EmailRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/mobile/auth")
@RequiredArgsConstructor
@Tag(name = "Mobile Auth", description = "移动端邮箱注册认证")
public class MobileAuthController {
    private final EmailRegistrationService emailRegistrationService;

    @PostMapping("/verification-code")
    @Operation(summary = "发送邮箱验证码", description = "向指定邮箱发送6位数字验证码，5分钟有效")
    public ApiResponse<VerificationCodeResponse> sendVerificationCode(
            @Valid @RequestBody EmailSendCodeRequest request) {
        return ApiResponse.success(emailRegistrationService.sendCode(request));
    }

    @PostMapping("/register")
    @Operation(summary = "邮箱验证码注册", description = "验证验证码并创建用户，返回JWT token")
    public ApiResponse<AuthTokenResponse> register(
            @Valid @RequestBody EmailRegisterRequest request,
            HttpServletRequest servletRequest) {
        try {
            String ip = resolveClientIp(servletRequest);
            return ApiResponse.success(emailRegistrationService.register(request, ip));
        } catch (Exception e) {
            log.error("Email registration failed: {}", e.getMessage(), e);
            throw e;
        }
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
