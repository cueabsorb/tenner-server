package com.irallyin.server.core.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.common.security.JwtTokenProvider;
import com.irallyin.server.core.auth.dto.AuthTokenResponse;
import com.irallyin.server.core.auth.dto.EmailSendCodeRequest;
import com.irallyin.server.core.auth.dto.EmailRegisterRequest;
import com.irallyin.server.core.auth.dto.UserProfileResponse;
import com.irallyin.server.core.auth.dto.VerificationCodeResponse;
import com.irallyin.server.data.entity.LinkedAccountEntity;
import com.irallyin.server.data.entity.RefreshTokenEntity;
import com.irallyin.server.data.entity.UserEntity;
import com.irallyin.server.data.mapper.LinkedAccountMapper;
import com.irallyin.server.data.mapper.RefreshTokenMapper;
import com.irallyin.server.data.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRegistrationService {
    private static final String EMAIL_PROVIDER = "email";
    private static final String REGISTER_SCENE = "register";
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final StringRedisTemplate stringRedisTemplate;
    private final EmailVerificationSender emailVerificationSender;
    private final UserMapper userMapper;
    private final LinkedAccountMapper linkedAccountMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    public VerificationCodeResponse sendCode(EmailSendCodeRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        try {
            stringRedisTemplate.opsForValue().set(buildRegisterCodeKey(email), code, CODE_TTL);
        } catch (Exception e) {
            log.error("Failed to store registration code in Redis for email={}: {}", email, e.getMessage(), e);
            throw new BusinessException(10025, "验证码存储失败，请稍后重试");
        }

        emailVerificationSender.sendCode(email, code);

        return VerificationCodeResponse.builder()
                .identifier(email)
                .scene(REGISTER_SCENE)
                .expiresInSeconds(CODE_TTL.toSeconds())
                .build();
    }

    @Transactional
    public AuthTokenResponse register(EmailRegisterRequest request, String ipAddress) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String code = request.getCode();

        // 1. 验证验证码
        String redisKey = buildRegisterCodeKey(email);
        String storedCode;
        try {
            storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            log.error("Failed to retrieve registration code from Redis for email={}: {}", email, e.getMessage(), e);
            throw new BusinessException(10025, "验证码校验失败，请稍后重试");
        }
        if (!StringUtils.hasText(storedCode)) {
            throw new BusinessException(10021, "验证码已过期或不存在");
        }
        if (!storedCode.equals(code)) {
            log.error("Registration code mismatch for email={}", email);
            throw new BusinessException(10022, "验证码错误，请输入正确验证码。");
        }
        stringRedisTemplate.delete(redisKey);

        // 2. 已有用户直接登录，不再创建新用户
        UserEntity existingUser = findExistingUserByEmail(email);
        if (existingUser != null) {
            touchEmailLinkedAccount(existingUser, email);
            return issueTokens(existingUser, null, null);
        }

        // 3. 新邮箱创建用户
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setDisplayName(resolveDisplayName(email));
        user.setLocale("zh-CN");
        user.setTimezone("Asia/Shanghai");
        user.setOnboardingCompleted(false);
        user.setOnboardingStep(0);
        user.setAccessStatus(0);
        user.setStatus(0);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);

        // 4. 创建邮箱关联账号
        LinkedAccountEntity linked = new LinkedAccountEntity();
        linked.setId(UUID.randomUUID().toString());
        linked.setUserId(user.getId());
        linked.setProvider(EMAIL_PROVIDER);
        linked.setProviderUserId(email);
        linked.setProviderEmail(email);
        linked.setProviderEmailVerified(true);
        linked.setLinkedAt(now);
        linked.setLastLoginAt(now);
        linked.setStatus(0);
        linkedAccountMapper.insert(linked);

        // 5. 签发 token
        return issueTokens(user, null, null);
    }

    private UserEntity findExistingUserByEmail(String email) {
        LinkedAccountEntity existingLink = linkedAccountMapper.selectOne(
                new LambdaQueryWrapper<LinkedAccountEntity>()
                        .eq(LinkedAccountEntity::getProvider, EMAIL_PROVIDER)
                        .eq(LinkedAccountEntity::getProviderEmail, email));
        if (existingLink != null) {
            UserEntity linkedUser = userMapper.selectById(existingLink.getUserId());
            if (linkedUser != null) {
                return linkedUser;
            }
        }

        return userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getEmail, email));
    }

    private void touchEmailLinkedAccount(UserEntity user, String email) {
        LinkedAccountEntity existingLink = linkedAccountMapper.selectOne(
                new LambdaQueryWrapper<LinkedAccountEntity>()
                        .eq(LinkedAccountEntity::getProvider, EMAIL_PROVIDER)
                        .eq(LinkedAccountEntity::getProviderEmail, email));
        if (existingLink != null) {
            existingLink.setLastLoginAt(LocalDateTime.now());
            linkedAccountMapper.updateById(existingLink);
            return;
        }

        LinkedAccountEntity linked = new LinkedAccountEntity();
        linked.setId(UUID.randomUUID().toString());
        linked.setUserId(user.getId());
        linked.setProvider(EMAIL_PROVIDER);
        linked.setProviderUserId(email);
        linked.setProviderEmail(email);
        linked.setProviderEmailVerified(true);
        linked.setLinkedAt(LocalDateTime.now());
        linked.setLastLoginAt(LocalDateTime.now());
        linked.setStatus(0);
        linkedAccountMapper.insert(linked);
    }

    private String buildRegisterCodeKey(String email) {
        return email + REGISTER_SCENE;
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

    private String resolveDisplayName(String email) {
        String localPart = email.split("@")[0];
        return StringUtils.hasText(localPart) ? localPart : "来嘞用户";
    }

    private UserProfileResponse toUserProfile(UserEntity user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .onboardingCompleted(Boolean.TRUE.equals(user.getOnboardingCompleted()))
                .build();
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
