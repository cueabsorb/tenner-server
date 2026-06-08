package com.irallyin.server.data.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("linked_accounts")
public class LinkedAccountEntity {
    @TableId
    private String id;
    private String userId;
    private String provider;
    private String providerUserId;
    private String providerEmail;
    private Boolean providerEmailVerified;
    private String providerNickname;
    private String providerAvatarUrl;
    private String providerLocale;
    private String providerScope;
    private LocalDateTime providerLinkUpdatedAt;
    private LocalDateTime linkedAt;
    private LocalDateTime lastLoginAt;
    private Integer status;
}
