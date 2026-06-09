-- ============================================================
-- IRallyIn: ir_auth schema - 认证与账号
-- Target: PolarDB MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_auth` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_auth`;

-- -----------------------------------------------------------
-- 1. users - 用户主表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `phone`                 VARCHAR(20)     NULL     COMMENT 'E.164格式 如+86-13800138000',
    `email`                 VARCHAR(255)    NULL     COMMENT 'RFC 5322',
    `password_hash`         VARCHAR(255)    NULL     COMMENT 'Argon2id hash, 仅邮箱注册',
    `display_name`          VARCHAR(50)     NOT NULL COMMENT '2-20字符',
    `avatar_url`            VARCHAR(512)    NULL     COMMENT 'OSS URL',
    `country`               VARCHAR(100)    NULL     COMMENT '国家/地区',
    `city`                  VARCHAR(100)    NULL     COMMENT '城市',
    `locale`                VARCHAR(10)     NOT NULL DEFAULT 'zh-CN' COMMENT 'IETF语言标签',
    `timezone`              VARCHAR(50)     NOT NULL DEFAULT 'Asia/Shanghai' COMMENT 'IANA时区',
    `onboarding_completed`  TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否完成新手引导',
    `onboarding_step`       TINYINT         NOT NULL DEFAULT 0 COMMENT '0=未开始 1-5=各步骤',
    `player_identity`       ENUM('amateur','professional','coach','coCoach') NULL COMMENT '球员身份',
    `ntrp_rating`           DECIMAL(3,1)    NULL     COMMENT '用户自评NTRP等级 1.0-5.5',
    `sys_ntrp_rating`       DECIMAL(3,1)    NULL     COMMENT '系统根据历史数据计算的NTRP等级',
    `access_status`         TINYINT         NOT NULL DEFAULT 0 COMMENT '用户访问状态: 0=正常可用, 1=禁止访问, 2=部分功能禁止(不能搜索/查看/上传等)',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`            TIMESTAMP       NULL     COMMENT '软删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_access_status` (`access_status`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted_at` (`deleted_at`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户主表';

-- -----------------------------------------------------------
-- 2. linked_accounts - 第三方账号绑定
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `linked_accounts` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `provider`              ENUM('phone','wechat','alipay','taobao','google','apple','email') NOT NULL COMMENT '认证提供商',
    `provider_user_id`      VARCHAR(255)    NOT NULL COMMENT '第三方平台用户ID',
    `provider_email`        VARCHAR(255)    NULL     COMMENT 'Apple隐藏邮箱等',
    `provider_nickname`     VARCHAR(100)    NULL     COMMENT '第三方昵称',
    `provider_avatar_url`   VARCHAR(512)    NULL     COMMENT '第三方头像',
    `linked_at`             TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    `last_login_at`         TIMESTAMP       NULL     COMMENT '最后登录时间',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_provider_user` (`provider`, `provider_user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_last_login_at` (`last_login_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方账号绑定';

-- -----------------------------------------------------------
-- 3. refresh_tokens - 刷新令牌
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `refresh_tokens` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `token_hash`            VARCHAR(128)    NOT NULL COMMENT 'SHA-256摘要',
    `device_id`             VARCHAR(255)    NULL     COMMENT '客户端设备标识',
    `device_info`           VARCHAR(500)    NULL     COMMENT '用户代理/设备描述',
    `expires_at`            TIMESTAMP       NOT NULL COMMENT '30天有效期',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `revoked_at`            TIMESTAMP       NULL     COMMENT '吊销时间, NULL=活跃',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token_hash` (`token_hash`),
    KEY `idx_user_id_expires` (`user_id`, `expires_at`),
    KEY `idx_status` (`status`),
    KEY `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='刷新令牌';

-- -----------------------------------------------------------
-- 4. verification_codes - 验证码
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `verification_codes` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `target`                VARCHAR(255)    NOT NULL COMMENT '手机号或邮箱地址',
    `code_hash`             VARCHAR(128)    NOT NULL COMMENT '验证码SHA-256摘要',
    `purpose`               ENUM('register','login','reset_password') NOT NULL COMMENT '用途',
    `expires_at`            TIMESTAMP       NOT NULL COMMENT '10分钟有效期',
    `used_at`               TIMESTAMP       NULL     COMMENT '使用时间',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ip_address`            VARCHAR(45)     NULL     COMMENT '请求IP',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_target_purpose` (`target`, `purpose`, `created_at` DESC),
    KEY `idx_status` (`status`),
    KEY `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码';

-- -----------------------------------------------------------
-- 5. login_audit_log - 登录审计日志 (按月分区)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `login_audit_log` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NULL     COMMENT '登录失败时为NULL',
    `provider`              VARCHAR(20)     NOT NULL COMMENT '登录方式',
    `ip_address`            VARCHAR(45)     NOT NULL COMMENT 'IP地址',
    `device_info`           VARCHAR(500)    NULL     COMMENT '设备信息',
    `success`               TINYINT(1)      NOT NULL COMMENT '是否成功',
    `created_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`, `created_at`),
    KEY `idx_user_id_created` (`user_id`, `created_at` DESC),
    KEY `idx_ip_created` (`ip_address`, `created_at` DESC),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录审计日志'
PARTITION BY RANGE (TO_DAYS(`created_at`)) (
    PARTITION p202606 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p202607 VALUES LESS THAN (TO_DAYS('2026-08-01')),
    PARTITION p202608 VALUES LESS THAN (TO_DAYS('2026-09-01')),
    PARTITION p202609 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p202610 VALUES LESS THAN (TO_DAYS('2026-11-01')),
    PARTITION p202611 VALUES LESS THAN (TO_DAYS('2026-12-01')),
    PARTITION p202612 VALUES LESS THAN (TO_DAYS('2027-01-01')),
    PARTITION p202701 VALUES LESS THAN (TO_DAYS('2027-02-01')),
    PARTITION p202702 VALUES LESS THAN (TO_DAYS('2027-03-01')),
    PARTITION p202703 VALUES LESS THAN (TO_DAYS('2027-04-01')),
    PARTITION p202704 VALUES LESS THAN (TO_DAYS('2027-05-01')),
    PARTITION p202705 VALUES LESS THAN (TO_DAYS('2027-06-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
