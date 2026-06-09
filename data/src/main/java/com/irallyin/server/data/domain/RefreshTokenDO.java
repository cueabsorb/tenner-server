package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 刷新令牌。
 */
@Data
@TableName("refresh_tokens")
public class RefreshTokenDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * UUID v4。
     */
    @TableId
    private String id;
    /**
     * 关联用户ID。
     */
    private String userId;
    /**
     * SHA-256摘要。
     */
    private String tokenHash;
    /**
     * 客户端设备标识。
     */
    private String deviceId;
    /**
     * 用户代理/设备描述。
     */
    private String deviceInfo;
    /**
     * 30天有效期。
     */
    private LocalDateTime expiresAt;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 吊销时间, NULL=活跃。
     */
    private LocalDateTime revokedAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
