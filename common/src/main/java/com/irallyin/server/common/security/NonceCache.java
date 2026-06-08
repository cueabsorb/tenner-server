package com.irallyin.server.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class NonceCache {
    private final StringRedisTemplate redisTemplate;
    private static final String NONCE_KEY_PREFIX = "request-signature:nonce:";

    public boolean exists(String nonce) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(NONCE_KEY_PREFIX + nonce));
        } catch (Exception e) {
            log.error("Failed to check nonce existence in Redis: {}", e.getMessage(), e);
            return false;
        }
    }

    public void store(String nonce, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(NONCE_KEY_PREFIX + nonce, "1", Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.error("Failed to store nonce in Redis: {}", e.getMessage(), e);
        }
    }
}
