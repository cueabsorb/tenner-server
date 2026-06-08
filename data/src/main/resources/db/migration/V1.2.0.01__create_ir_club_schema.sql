-- ============================================================
-- IRallyIn: ir_club schema - 俱乐部
-- Target: PolarDB MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_club` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_club`;

-- -----------------------------------------------------------
-- 1. clubs - 俱乐部
-- name 同城唯一, 每用户最多创建3个
-- -----------------------------------------------------------
CREATE TABLE `clubs` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `name`                  VARCHAR(50)     NOT NULL COMMENT '俱乐部名称(同城唯一)',
    `english_name`          VARCHAR(50)     NULL     COMMENT '英文名',
    `city_code`             VARCHAR(10)     NULL     COMMENT '城市编码',
    `description`           TEXT            NULL     COMMENT '描述',
    `cover_image_url`       VARCHAR(512)    NULL     COMMENT 'OSS封面图URL',
    `owner_id`              CHAR(36)        NOT NULL COMMENT '创建者ID',
    `join_policy`           ENUM('open','approval','invite_only') NOT NULL DEFAULT 'approval' COMMENT '加入策略',
    `member_count`          INT             NOT NULL DEFAULT 0 COMMENT '成员数(异步回写)',
    `club_status`           ENUM('active','suspended','dissolved') NOT NULL DEFAULT 'active' COMMENT '俱乐部状态',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name_city` (`name`, `city_code`),
    KEY `idx_city_code` (`city_code`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_status` (`status`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='俱乐部';

-- -----------------------------------------------------------
-- 2. club_members - 俱乐部成员
-- -----------------------------------------------------------
CREATE TABLE `club_members` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `club_id`               CHAR(36)        NOT NULL COMMENT '关联clubs.id',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `role`                  ENUM('owner','admin','member','visitor') NOT NULL DEFAULT 'member' COMMENT '角色',
    `member_status`         ENUM('active','removed','left') NOT NULL DEFAULT 'active' COMMENT '成员状态',
    `joined_at`             TIMESTAMP       NULL     COMMENT '加入时间',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_club_user` (`club_id`, `user_id`),
    KEY `idx_user_id` (`user_id`, `member_status`),
    KEY `idx_status` (`status`),
    KEY `idx_club_role` (`club_id`, `role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='俱乐部成员';

-- -----------------------------------------------------------
-- 3. club_join_requests - 入会申请
-- -----------------------------------------------------------
CREATE TABLE `club_join_requests` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `club_id`               CHAR(36)        NOT NULL COMMENT '关联clubs.id',
    `user_id`               CHAR(36)        NOT NULL COMMENT '申请用户ID',
    `message`               TEXT            NULL     COMMENT '申请留言',
    `request_status`        ENUM('pending','approved','rejected','cancelled') NOT NULL DEFAULT 'pending' COMMENT '申请状态',
    `reviewed_by`           CHAR(36)        NULL     COMMENT '审核人ID',
    `reviewed_at`           TIMESTAMP       NULL     COMMENT '审核时间',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_club_request_status` (`club_id`, `request_status`),
    KEY `idx_status` (`status`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入会申请';

-- -----------------------------------------------------------
-- 4. club_events - 俱乐部活动
-- -----------------------------------------------------------
CREATE TABLE `club_events` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `club_id`               CHAR(36)        NOT NULL COMMENT '关联clubs.id',
    `organizer_id`          CHAR(36)        NOT NULL COMMENT '组织者ID',
    `title`                 VARCHAR(100)    NOT NULL COMMENT '活动标题',
    `court_id`              CHAR(36)        NULL     COMMENT '球场ID',
    `court_name`            VARCHAR(200)    NULL     COMMENT '球场名称(冗余)',
    `started_at`            TIMESTAMP       NOT NULL COMMENT '开始时间',
    `ended_at`              TIMESTAMP       NULL     COMMENT '结束时间',
    `max_participants`      SMALLINT        NULL     COMMENT '最大参与人数',
    `event_status`          ENUM('upcoming','ongoing','completed','cancelled') NOT NULL DEFAULT 'upcoming' COMMENT '活动状态',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_club_started` (`club_id`, `started_at` DESC),
    KEY `idx_event_status_started` (`event_status`, `started_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='俱乐部活动';
