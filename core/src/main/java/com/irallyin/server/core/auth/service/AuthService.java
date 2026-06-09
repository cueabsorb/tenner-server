package com.irallyin.server.core.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.common.security.JwtTokenProvider;
import com.irallyin.server.core.auth.dto.*;
import com.irallyin.server.data.domain.LinkedAccountDO;
import com.irallyin.server.data.domain.LoginAuditLogDO;
import com.irallyin.server.data.domain.RefreshTokenDO;
import com.irallyin.server.data.domain.UserDO;
import com.irallyin.server.data.mapper.LinkedAccountMapper;
import com.irallyin.server.data.mapper.LoginAuditLogMapper;
import com.irallyin.server.data.mapper.RefreshTokenMapper;
import com.irallyin.server.data.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String GOOGLE_PROVIDER = "google";
    private static final long REFRESH_REPLAY_GRACE_SECONDS = 60;

    private final GoogleOAuthClient googleOAuthClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final LinkedAccountMapper linkedAccountMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final LoginAuditLogMapper loginAuditLogMapper;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Transactional
    public AuthTokenResponse loginWithGoogle(GoogleLoginRequest request, String ipAddress) {
        try {
            GoogleTokenResponse googleToken = googleOAuthClient.exchangeAuthorizationCode(
                    request.getAuthorizationCode(),
                    request.getRedirectUri(),
                    request.getCodeVerifier()
            );
            GoogleTokenInfoResponse googleUser = googleOAuthClient.verifyIdToken(googleToken.getIdToken());

            UserDO user = findOrCreateUser(googleUser);
            upsertGoogleLinkedAccount(user.getId(), googleUser, googleToken.getScope());
            AuthTokenResponse response = issueTokens(user, request.getDeviceId(), request.getDeviceInfo());
            writeAudit(user.getId(), ipAddress, request.getDeviceInfo(), true);
            return response;
        } catch (RuntimeException e) {
            log.error("Google login failed for ip={}: {}", ipAddress, e.getMessage(), e);
            writeAudit(null, ipAddress, request.getDeviceInfo(), false);
            throw e;
        }
    }

    @Transactional
    public AuthTokenResponse refresh(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BusinessException(10002, "刷新令牌无效或已过期");
        }

        String tokenHash = sha256(request.getRefreshToken());
        RefreshTokenDO stored = findRefreshTokenForSession(tokenHash);
        if (stored == null) {
            throw new BusinessException(10002, "刷新令牌无效或已过期");
        }
        if (StringUtils.hasText(request.getDeviceId())
                && StringUtils.hasText(stored.getDeviceId())
                && !request.getDeviceId().equals(stored.getDeviceId())) {
            throw new BusinessException(10002, "刷新令牌设备不匹配");
        }

        if (stored.getRevokedAt() != null) {
            log.warn("Reactivating unexpired refresh token for user={} to preserve app session", stored.getUserId());
            stored.setRevokedAt(null);
            refreshTokenMapper.updateById(stored);
        }

        UserDO user = findActiveUserById(stored.getUserId());
        return issueAccessToken(user, request.getRefreshToken());
    }

    private RefreshTokenDO findRefreshTokenForSession(String tokenHash) {
        LocalDateTime now = LocalDateTime.now();
        RefreshTokenDO stored = refreshTokenMapper.selectOne(new LambdaQueryWrapper<RefreshTokenDO>()
                .eq(RefreshTokenDO::getTokenHash, tokenHash)
                .gt(RefreshTokenDO::getExpiresAt, now)
                .last("LIMIT 1"));
        return stored;
    }

    public UserProfileResponse getCurrentUser(String userId) {
        return toUserProfile(findActiveUserById(userId));
    }

    private UserDO findOrCreateUser(GoogleTokenInfoResponse googleUser) {
        LinkedAccountDO linked = linkedAccountMapper.selectOne(new LambdaQueryWrapper<LinkedAccountDO>()
                .eq(LinkedAccountDO::getProvider, GOOGLE_PROVIDER)
                .eq(LinkedAccountDO::getProviderUserId, googleUser.getSub()));
        if (linked != null) {
            UserDO existing = findActiveUserById(linked.getUserId());
            updateUserFromGoogle(existing, googleUser);
            return existing;
        }

        UserDO user = null;
        if (Boolean.TRUE.equals(googleUser.getEmailVerified()) && StringUtils.hasText(googleUser.getEmail())) {
            user = userMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                    .eq(UserDO::getEmail, googleUser.getEmail())
                    .isNull(UserDO::getDeletedAt));
        }
        if (user != null) {
            updateUserFromGoogle(user, googleUser);
            return user;
        }

        user = new UserDO();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(Boolean.TRUE.equals(googleUser.getEmailVerified()) ? googleUser.getEmail() : null);
        user.setDisplayName(resolveDisplayName(googleUser));
        user.setAvatarUrl(googleUser.getPicture());
        user.setLocale(StringUtils.hasText(googleUser.getLocale()) ? googleUser.getLocale() : "zh-CN");
        user.setTimezone("Asia/Shanghai");
        user.setStatus(0);
        user.setOnboardingCompleted(false);
        user.setOnboardingStep(0);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);
        return user;
    }

    private void updateUserFromGoogle(UserDO user, GoogleTokenInfoResponse googleUser) {
        boolean changed = false;
        if (Boolean.TRUE.equals(googleUser.getEmailVerified())
                && StringUtils.hasText(googleUser.getEmail())
                && !googleUser.getEmail().equals(user.getEmail())) {
            user.setEmail(googleUser.getEmail());
            changed = true;
        }
        if (!StringUtils.hasText(user.getDisplayName()) && StringUtils.hasText(googleUser.getName())) {
            user.setDisplayName(googleUser.getName());
            changed = true;
        }
        if (!StringUtils.hasText(user.getAvatarUrl()) && StringUtils.hasText(googleUser.getPicture())) {
            user.setAvatarUrl(googleUser.getPicture());
            changed = true;
        }
        if (changed) {
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    private void upsertGoogleLinkedAccount(String userId, GoogleTokenInfoResponse googleUser, String scope) {
        LocalDateTime now = LocalDateTime.now();
        LinkedAccountDO linked = linkedAccountMapper.selectOne(new LambdaQueryWrapper<LinkedAccountDO>()
                .eq(LinkedAccountDO::getProvider, GOOGLE_PROVIDER)
                .eq(LinkedAccountDO::getProviderUserId, googleUser.getSub()));
        if (linked == null) {
            linked = new LinkedAccountDO();
            linked.setId(UUID.randomUUID().toString());
            linked.setUserId(userId);
            linked.setProvider(GOOGLE_PROVIDER);
            linked.setProviderUserId(googleUser.getSub());
            linked.setLinkedAt(now);
        }
        linked.setProviderEmail(googleUser.getEmail());
        linked.setProviderEmailVerified(Boolean.TRUE.equals(googleUser.getEmailVerified()));
        linked.setProviderNickname(googleUser.getName());
        linked.setProviderAvatarUrl(googleUser.getPicture());
        linked.setProviderLocale(googleUser.getLocale());
        linked.setProviderScope(scope);
        linked.setProviderLinkUpdatedAt(now);
        linked.setLastLoginAt(now);
        if (linked.getId() == null) {
            linkedAccountMapper.insert(linked);
        } else {
            int updated = linkedAccountMapper.updateById(linked);
            if (updated == 0) {
                linkedAccountMapper.insert(linked);
            }
        }
    }

    private AuthTokenResponse issueTokens(UserDO user, String deviceId, String deviceInfo) {
        UUID userId = UUID.fromString(user.getId());
        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        RefreshTokenDO storedRefreshToken = new RefreshTokenDO();
        storedRefreshToken.setId(UUID.randomUUID().toString());
        storedRefreshToken.setUserId(user.getId());
        storedRefreshToken.setTokenHash(sha256(refreshToken));
        storedRefreshToken.setDeviceId(deviceId);
        storedRefreshToken.setDeviceInfo(deviceInfo);
        storedRefreshToken.setCreatedAt(LocalDateTime.now());
        storedRefreshToken.setExpiresAt(LocalDateTime.ofInstant(
                Instant.now().plusMillis(refreshTokenExpirationMs),
                ZoneOffset.UTC
        ));
        refreshTokenMapper.insert(storedRefreshToken);

        return AuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .user(toUserProfile(user))
                .build();
    }

    private AuthTokenResponse issueAccessToken(UserDO user, String refreshToken) {
        UUID userId = UUID.fromString(user.getId());
        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        return AuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .user(toUserProfile(user))
                .build();
    }

    private UserDO findActiveUserById(String userId) {
        UserDO user = userMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getId, userId)
                .isNull(UserDO::getDeletedAt));
        if (user == null) {
            throw new BusinessException(10004, "用户不存在");
        }
        return user;
    }

    private UserProfileResponse toUserProfile(UserDO user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .intro(user.getBio())
                .country(user.getCountry())
                .province(user.getProvince())
                .city(user.getCity())
                .district(user.getDistrict())
                .ntrpRating(user.getNtrpRating())
                .onboardingCompleted(Boolean.TRUE.equals(user.getOnboardingCompleted()))
                .build();
    }

    private String resolveDisplayName(GoogleTokenInfoResponse googleUser) {
        if (StringUtils.hasText(googleUser.getName())) {
            return googleUser.getName();
        }
        if (StringUtils.hasText(googleUser.getEmail())) {
            return googleUser.getEmail().split("@")[0];
        }
        return "来嘞用户";
    }

    private void writeAudit(String userId, String ipAddress, String deviceInfo, boolean success) {
        LoginAuditLogDO auditLog = new LoginAuditLogDO();
        auditLog.setId(UUID.randomUUID().toString());
        auditLog.setUserId(userId);
        auditLog.setProvider(GOOGLE_PROVIDER);
        auditLog.setIpAddress(StringUtils.hasText(ipAddress) ? ipAddress : "0.0.0.0");
        auditLog.setDeviceInfo(deviceInfo);
        auditLog.setSuccess(success);
        auditLog.setCreatedAt(LocalDateTime.now());
        loginAuditLogMapper.insert(auditLog);
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
