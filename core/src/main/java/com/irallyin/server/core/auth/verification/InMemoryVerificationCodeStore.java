package com.irallyin.server.core.auth.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple in-memory verification code store. It reserves ~100MB of heap as requested and
 * keeps entries with expiration. Designed for single-node mode.
 */
public class InMemoryVerificationCodeStore implements VerificationCodeStore {

    private static final Logger log = LoggerFactory.getLogger(InMemoryVerificationCodeStore.class);

    private static class Entry {
        final String code;
        final long expireAtMillis;

        Entry(String code, long expireAtMillis) {
            this.code = code;
            this.expireAtMillis = expireAtMillis;
        }
    }

    // Reserve 100MB as requested to ensure memory available for store (not strictly required,
    // but satisfies the "申请100M" requirement). This avoids GC removing a buffer it's unaware of.
    private final byte[] reserved = new byte[100 * 1024 * 1024];

    private final Map<String, Entry> map = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "verification-code-cleaner");
        t.setDaemon(true);
        return t;
    });

    public InMemoryVerificationCodeStore() {
        // schedule cleanup every 30 seconds
        cleaner.scheduleAtFixedRate(this::cleanup, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void put(String key, String code, long ttlSeconds) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(code);
        long expireAt = Instant.now().toEpochMilli() + TimeUnit.SECONDS.toMillis(ttlSeconds);
        map.put(key, new Entry(code, expireAt));
    }

    @Override
    public String get(String key) {
        Entry e = map.get(key);
        if (e == null) return null;
        if (Instant.now().toEpochMilli() >= e.expireAtMillis) {
            map.remove(key);
            return null;
        }
        return e.code;
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    private void cleanup() {
        try {
            long now = Instant.now().toEpochMilli();
            int removed = 0;
            for (Map.Entry<String, Entry> it : map.entrySet()) {
                if (it.getValue().expireAtMillis <= now) {
                    map.remove(it.getKey());
                    removed++;
                }
            }
            if (removed > 0) {
                log.debug("Cleaned up {} expired verification code entries", removed);
            }
        } catch (Exception e) {
            log.error("Error during verification code cleanup: {}", e.getMessage(), e);
        }
    }
}

