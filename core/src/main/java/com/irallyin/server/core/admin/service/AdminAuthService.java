package com.irallyin.server.core.admin.service;

import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.core.admin.dto.AdminLoginRequest;
import com.irallyin.server.core.admin.dto.AdminLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminProperties adminProperties;
    private final AdminTokenService adminTokenService;

    public AdminLoginResponse login(AdminLoginRequest request) {
        if (!adminProperties.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new BusinessException(10003, "无权限访问后台");
        }
        if (StringUtils.hasText(adminProperties.getPassword())
                && !adminProperties.getPassword().equals(request.getPassword())) {
            throw new BusinessException(10002, "管理员账号或密码错误");
        }

        return AdminLoginResponse.builder()
                .token(adminTokenService.generateToken(adminProperties.getEmail()))
                .tokenType("Bearer")
                .expiresIn(adminProperties.getTokenExpirationMs() / 1000)
                .email(adminProperties.getEmail())
                .build();
    }
}
