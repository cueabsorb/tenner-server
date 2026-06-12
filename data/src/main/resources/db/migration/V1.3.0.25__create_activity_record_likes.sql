USE `ir_activity`;

CREATE TABLE IF NOT EXISTS `activity_record_like_stats` (
    `session_id`            CHAR(36)        NOT NULL COMMENT '关联play_sessions.id',
    `owner_id`              CHAR(36)        NOT NULL COMMENT '活动记录作者ID',
    `total_like_count`      INT             NOT NULL DEFAULT 0 COMMENT '点赞总次数',
    `last_liked_at`         DATETIME        NULL     COMMENT '最近点赞时间',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`session_id`),
    KEY `idx_owner_total_likes` (`owner_id`, `total_like_count` DESC),
    KEY `idx_last_liked_at` (`last_liked_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='精彩记录点赞总表';

CREATE TABLE IF NOT EXISTS `activity_record_likes` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `session_id`            CHAR(36)        NOT NULL COMMENT '关联play_sessions.id',
    `liker_user_id`         CHAR(36)        NOT NULL COMMENT '点赞用户ID',
    `like_count`            INT             NOT NULL DEFAULT 1 COMMENT '该用户点赞次数',
    `first_liked_at`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次点赞时间',
    `last_liked_at`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近点赞时间',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_record_liker` (`session_id`, `liker_user_id`),
    KEY `idx_session_recent` (`session_id`, `last_liked_at` DESC),
    KEY `idx_liker_recent` (`liker_user_id`, `last_liked_at` DESC),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='精彩记录点赞明细';
