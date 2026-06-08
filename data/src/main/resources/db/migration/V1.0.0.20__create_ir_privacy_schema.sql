-- ============================================================
-- IRallyIn: ir_privacy schema - 隐私权限
-- Target: PolarDB MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_privacy` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_privacy`;

-- -----------------------------------------------------------
-- 1. privacy_settings - 隐私设置 (每用户每类别一行)
-- -----------------------------------------------------------
CREATE TABLE `privacy_settings` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联ir_auth.users.id',
    `category`              ENUM('profile','location','schedule','skill','equipment','matchRequest','search') NOT NULL COMMENT '隐私类别',
    `visibility`            ENUM('nobody','matchedPlayers','all') NOT NULL DEFAULT 'matchedPlayers' COMMENT '可见级别',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_category` (`user_id`, `category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='隐私设置';

-- -----------------------------------------------------------
-- 2. resource_privacy - 资源级可见性覆盖
-- -----------------------------------------------------------
CREATE TABLE `resource_privacy` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `owner_id`              CHAR(36)        NOT NULL COMMENT '资源所有者ID',
    `resource_type`         VARCHAR(30)     NOT NULL COMMENT '资源类型如feed_post/play_session',
    `resource_id`           CHAR(36)        NOT NULL COMMENT '资源ID(多态)',
    `visibility`            ENUM('nobody','matchedPlayers','all') NOT NULL COMMENT '可见级别',
    `allowed_club_ids`      JSON            NULL     COMMENT '允许可见的俱乐部ID列表',
    `allowed_user_ids`      JSON            NULL     COMMENT '允许可见的用户ID列表',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_resource` (`resource_type`, `resource_id`),
    KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源级可见性';

-- -----------------------------------------------------------
-- 3. block_relations - 拉黑关系
-- -----------------------------------------------------------
CREATE TABLE `block_relations` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `blocker_id`            CHAR(36)        NOT NULL COMMENT '拉黑者ID',
    `blocked_user_id`       CHAR(36)        NOT NULL COMMENT '被拉黑者ID',
    `reason`                VARCHAR(200)    NULL     COMMENT '拉黑原因',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_blocker_blocked` (`blocker_id`, `blocked_user_id`),
    KEY `idx_blocked_user` (`blocked_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拉黑关系';

-- -----------------------------------------------------------
-- 4. user_reports - 用户举报 (追加型)
-- -----------------------------------------------------------
CREATE TABLE `user_reports` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `reporter_id`           CHAR(36)        NOT NULL COMMENT '举报人ID',
    `reported_user_id`      CHAR(36)        NOT NULL COMMENT '被举报人ID',
    `report_type`           ENUM('harassment','spam','inappropriateContent','fakeProfile','other') NOT NULL COMMENT '举报类型',
    `description`           TEXT            NULL     COMMENT '举报描述',
    `resource_type`         VARCHAR(30)     NULL     COMMENT '关联资源类型如feed_post',
    `resource_id`           CHAR(36)        NULL     COMMENT '关联资源ID',
    `review_status`         ENUM('pending','reviewing','resolved','dismissed') NOT NULL DEFAULT 'pending' COMMENT '处理状态',
    `reviewed_by`           CHAR(36)        NULL     COMMENT '审核管理员ID',
    `reviewed_at`           TIMESTAMP       NULL     COMMENT '审核时间',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_reported_user_review_status` (`reported_user_id`, `review_status`),
    KEY `idx_reporter_id` (`reporter_id`),
    KEY `idx_review_status_created` (`review_status`, `created_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户举报';

-- -----------------------------------------------------------
-- 5. account_deletion_requests - 账号注销请求
-- -----------------------------------------------------------
CREATE TABLE `account_deletion_requests` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '申请注销的用户ID',
    `reason`                TEXT            NULL     COMMENT '注销原因',
    `request_status`        ENUM('pending','processing','completed','cancelled') NOT NULL DEFAULT 'pending' COMMENT '注销请求状态',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `processed_at`          TIMESTAMP       NULL     COMMENT '处理完成时间',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_request_status` (`request_status`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号注销请求';
