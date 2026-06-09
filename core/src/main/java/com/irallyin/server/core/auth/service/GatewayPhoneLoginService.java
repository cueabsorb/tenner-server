package com.irallyin.server.core.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.irallyin.server.common.security.JwtTokenProvider;
import com.irallyin.server.core.auth.dto.AuthTokenResponse;
import com.irallyin.server.core.auth.dto.GatewayPhoneLoginRequest;
import com.irallyin.server.core.auth.dto.UserProfileResponse;
import com.irallyin.server.data.entity.LinkedAccountEntity;
import com.irallyin.server.data.entity.RefreshTokenEntity;
import com.irallyin.server.data.entity.UserEntity;
import com.irallyin.server.data.mapper.LinkedAccountMapper;
import com.irallyin.server.data.mapper.RefreshTokenMapper;
import com.irallyin.server.data.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatewayPhoneLoginService {
    private static final String PHONE_PROVIDER = "phone";

    private final PhoneNumberProvider phoneNumberProvider;
    private final UserMapper userMapper;
    private final LinkedAccountMapper linkedAccountMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Transactional
    public AuthTokenResponse login(GatewayPhoneLoginRequest request) {
        String phone = normalizePhone(phoneNumberProvider.getMobile(request.getAccessToken()));
        UserEntity user = findUserByPhone(phone);
        if (user == null) {
            user = createPhoneUser(phone);
        }
        touchPhoneLinkedAccount(user, phone);
        return issueTokens(user, request.getDeviceId(), request.getDeviceInfo());
    }

    private UserEntity findUserByPhone(String phone) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, phone)
                .eq(UserEntity::getStatus, 0)
                .last("LIMIT 1"));
    }

    private UserEntity createPhoneUser(String phone) {
        LocalDateTime now = LocalDateTime.now();
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID().toString());
        user.setPhone(phone);
        user.setDisplayName(maskPhone(phone));
        user.setLocale("zh-CN");
        user.setTimezone("Asia/Shanghai");
        user.setOnboardingCompleted(false);
        user.setOnboardingStep(0);
        user.setAccessStatus(0);
        user.setStatus(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);
        return user;
    }

    private void touchPhoneLinkedAccount(UserEntity user, String phone) {
        LocalDateTime now = LocalDateTime.now();
        LinkedAccountEntity linked = linkedAccountMapper.selectOne(new LambdaQueryWrapper<LinkedAccountEntity>()
                .eq(LinkedAccountEntity::getProvider, PHONE_PROVIDER)
                .eq(LinkedAccountEntity::getProviderUserId, phone)
                .eq(LinkedAccountEntity::getStatus, 0)
                .last("LIMIT 1"));
        boolean isNewLink = linked == null;
        if (linked == null) {
            linked = new LinkedAccountEntity();
            linked.setId(UUID.randomUUID().toString());
            linked.setUserId(user.getId());
            linked.setProvider(PHONE_PROVIDER);
            linked.setProviderUserId(phone);
            linked.setLinkedAt(now);
            linked.setStatus(0);
        }
        linked.setLastLoginAt(now);
        if (linked.getProviderLinkUpdatedAt() == null) {
            linked.setProviderLinkUpdatedAt(now);
        }
        if (isNewLink) {
            linkedAccountMapper.insert(linked);
        } else {
            linkedAccountMapper.updateById(linked);
        }
    }

    private AuthTokenResponse issueTokens(UserEntity user, String deviceId, String deviceInfo) {
        UUID userId = UUID.fromString(user.getId());
        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        RefreshTokenEntity storedRefreshToken = new RefreshTokenEntity();
        storedRefreshToken.setId(UUID.randomUUID().toString());
        storedRefreshToken.setUserId(user.getId());
        storedRefreshToken.setTokenHash(sha256(refreshToken));
        storedRefreshToken.setDeviceId(deviceId);
        storedRefreshToken.setDeviceInfo(deviceInfo);
        storedRefreshToken.setCreatedAt(LocalDateTime.now());
        storedRefreshToken.setExpiresAt(LocalDateTime.ofInstant(
                Instant.now().plusMillis(refreshTokenExpirationMs), ZoneOffset.UTC));
        refreshTokenMapper.insert(storedRefreshToken);

        return AuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .user(toUserProfile(user))
                .build();
    }

    private UserProfileResponse toUserProfile(UserEntity user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .onboardingCompleted(Boolean.TRUE.equals(user.getOnboardingCompleted()))
                .build();
    }

    private String normalizePhone(String phone) {
        String normalized = phone == null ? "" : phone.replaceAll("[^0-9+]", "");
        if (!StringUtils.hasText(normalized)) {
            throw new com.irallyin.server.common.exception.BusinessException(10033, "阿里云号码认证未返回手机号");
        }
        return normalized;
    }

    private String maskPhone(String phone) {
        if (phone.length() < 7) {
            return "手机用户";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
