package com.irallyin.server.common.cache;

/**
 * Redis Key 统一管理。
 *
 * <p>约定：
 * 1. 所有业务 Redis Key 必须通过这个类生成，避免散落在业务代码里难以维护。
 * 2. 带 TTL 的 Key 在注释里标明有效期；不带 TTL 的 Key 默认长期有效，由业务主动刷新或删除。
 * 3. 用户维度 Key 的 userId 必须使用 ir_auth.users.id，不能使用 email。</p>
 */
public final class RedisKeys {
    private RedisKeys() {
    }

    /**
     * 邮箱/手机号验证码 Key。
     * TTL：5 分钟。
     * 格式：verification-code:{identifier}:{scene}
     */
    public static String verificationCode(String identifier, String scene) {
        return "verification-code:" + identifier + ":" + scene;
    }

    /**
     * 请求签名 Nonce 去重 Key。
     * TTL：由调用方根据签名窗口传入。
     * 格式：request-signature:nonce:{nonce}
     */
    public static String requestSignatureNonce(String nonce) {
        return "request-signature:nonce:" + nonce;
    }

    /**
     * 用户关注数量缓存 Key。
     * TTL：长期有效，不设置过期时间；每次关注关系变化后重新写入。
     * 格式按当前移动端约定保留为：follow{userId}
     */
    public static String followingCount(String userId) {
        return "follow" + userId;
    }
}
