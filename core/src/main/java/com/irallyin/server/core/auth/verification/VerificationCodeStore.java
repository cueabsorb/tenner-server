package com.irallyin.server.core.auth.verification;

/**
 * Storage abstraction for verification codes.
 */
public interface VerificationCodeStore {
    /**
     * Store a code with given TTL in seconds.
     */
    void put(String key, String code, long ttlSeconds);

    /**
     * Get the stored code or null if missing/expired.
     */
    String get(String key);

    /**
     * Remove the stored code.
     */
    void remove(String key);
}

