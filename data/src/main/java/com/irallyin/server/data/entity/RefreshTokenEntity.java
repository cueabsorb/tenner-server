package com.irallyin.server.data.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("refresh_tokens")
public class RefreshTokenEntity {
    @TableId
    private String id;
    private String userId;
    private String tokenHash;
    private String deviceId;
    private String deviceInfo;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;
    private Integer status;
}
