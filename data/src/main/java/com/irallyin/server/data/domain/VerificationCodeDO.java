package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码。
 */
@Data
@TableName("verification_codes")
public class VerificationCodeDO implements Serializable {
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
     * 手机号或邮箱地址。
     */
    private String target;
    /**
     * 验证码SHA-256摘要。
     */
    private String codeHash;
    /**
     * 用途。
     */
    private String purpose;
    /**
     * 10分钟有效期。
     */
    private LocalDateTime expiresAt;
    /**
     * 使用时间。
     */
    private LocalDateTime usedAt;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 请求IP。
     */
    private String ipAddress;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
