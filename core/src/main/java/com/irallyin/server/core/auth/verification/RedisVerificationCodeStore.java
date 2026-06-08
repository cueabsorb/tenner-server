package com.irallyin.server.core.auth.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis-backed verification code store. Uses SETEX semantics to keep TTL in Redis.
 */
public class RedisVerificationCodeStore implements VerificationCodeStore {

    private static final Logger log = LoggerFactory.getLogger(RedisVerificationCodeStore.class);

    private final JedisPool pool;

    public RedisVerificationCodeStore(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public void put(String key, String code, long ttlSeconds) {
        try (Jedis j = pool.getResource()) {
            j.setex(key, (int) ttlSeconds, code);
        } catch (Exception e) {
            log.error("Failed to store verification code in Redis: key={}, error={}", key, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String get(String key) {
        try (Jedis j = pool.getResource()) {
            return j.get(key);
        } catch (Exception e) {
            log.error("Failed to get verification code from Redis: key={}, error={}", key, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void remove(String key) {
        try (Jedis j = pool.getResource()) {
            j.del(key);
        } catch (Exception e) {
            log.error("Failed to remove verification code from Redis: key={}, error={}", key, e.getMessage(), e);
            throw e;
        }
    }
}

