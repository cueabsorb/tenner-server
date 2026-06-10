package com.irallyin.server.core.auth.service;

import com.irallyin.server.common.cache.RedisKeys;
import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.core.auth.dto.SendVerificationCodeRequest;
import com.irallyin.server.core.auth.dto.VerificationCodeResponse;
import com.irallyin.server.core.auth.dto.VerifyCodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final StringRedisTemplate stringRedisTemplate;
    private final EmailVerificationSender emailVerificationSender;

    public VerificationCodeResponse sendCode(SendVerificationCodeRequest request) {
        String identifier = normalizeIdentifier(request.getIdentifier());
        String scene = normalizeScene(request.getScene());
        String code = generateCode();

        try {
            stringRedisTemplate.opsForValue().set(buildRedisKey(identifier, scene), code, CODE_TTL);
        } catch (Exception e) {
            log.error("Failed to store verification code in Redis for identifier={}: {}", identifier, e.getMessage(), e);
            throw new BusinessException(10025, "验证码存储失败，请稍后重试");
        }

        if (isEmail(identifier)) {
            emailVerificationSender.sendCode(identifier, code);
        }

        return VerificationCodeResponse.builder()
                .identifier(identifier)
                .scene(scene)
                .expiresInSeconds(CODE_TTL.toSeconds())
                .build();
    }

    public void verifyCode(VerifyCodeRequest request) {
        String identifier = normalizeIdentifier(request.getIdentifier());
        String scene = normalizeScene(request.getScene());
        String redisKey = buildRedisKey(identifier, scene);
        String storedCode;
        try {
            storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            log.error("Failed to retrieve verification code from Redis for identifier={}: {}", identifier, e.getMessage(), e);
            throw new BusinessException(10025, "验证码校验失败，请稍后重试");
        }

        if (!StringUtils.hasText(storedCode)) {
            throw new BusinessException(10021, "验证码已过期或不存在");
        }
        if (!storedCode.equals(request.getCode())) {
            log.error("Verification code mismatch for identifier={}", identifier);
            throw new BusinessException(10022, "验证码错误，请输入正确验证码。");
        }
        stringRedisTemplate.delete(redisKey);
    }

    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private String buildRedisKey(String identifier, String scene) {
        return RedisKeys.verificationCode(identifier, scene);
    }

    private String normalizeIdentifier(String identifier) {
        String normalized = identifier == null ? "" : identifier.trim();
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException(10023, "注册ID、手机号或邮箱不能为空");
        }
        return isEmail(normalized) ? normalized.toLowerCase(Locale.ROOT) : normalized;
    }

    private String normalizeScene(String scene) {
        String normalized = scene == null ? "" : scene.trim().toLowerCase(Locale.ROOT);
        if (!"register".equals(normalized) && !"login".equals(normalized)) {
            throw new BusinessException(10024, "验证码场景必须是 register 或 login");
        }
        return normalized;
    }

    private boolean isEmail(String identifier) {
        return identifier.contains("@");
    }
}
