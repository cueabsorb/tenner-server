package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录审计日志。
 */
@Data
@TableName("login_audit_log")
public class LoginAuditLogDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * UUID v4。
     */
    private String id;
    /**
     * 登录失败时为NULL。
     */
    private String userId;
    /**
     * 登录方式。
     */
    private String provider;
    /**
     * IP地址。
     */
    private String ipAddress;
    /**
     * 设备信息。
     */
    private String deviceInfo;
    /**
     * 是否成功。
     */
    private Boolean success;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
