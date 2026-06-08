package com.irallyin.server.data.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("login_audit_log")
public class LoginAuditLogEntity {
    @TableId
    private String id;
    private String userId;
    private String provider;
    private String ipAddress;
    private String deviceInfo;
    private Boolean success;
    private LocalDateTime createdAt;
    private Integer status;
}
