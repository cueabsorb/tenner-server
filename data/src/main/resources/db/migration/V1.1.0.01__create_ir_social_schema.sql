-- ============================================================
-- IRallyIn: ir_social schema - 内容流
-- Target: PolarDB MySQL 8.0+
-- 高增长表按季度分区
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_social` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_social`;

-- -----------------------------------------------------------
-- 1. feed_posts - 动态帖子 (按季度分区)
-- -----------------------------------------------------------
CREATE TABLE `feed_posts` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `author_id`             CHAR(36)        NOT NULL COMMENT '作者ID',
    `text`                  TEXT            NULL     COMMENT '帖子文字(≤500字)',
    `image_urls`            JSON            NULL     COMMENT '图片URL数组(1-9张)',
    `play_session_id`       CHAR(36)        NULL     COMMENT '关联运动记录ID',
    `court_id`              CHAR(36)        NULL     COMMENT '关联球场ID',
    `topic_tags`            JSON            NULL     COMMENT '话题标签数组',
    `visibility`            ENUM('nobody','matchedPlayers','all') NOT NULL DEFAULT 'matchedPlayers' COMMENT '可见范围',
    `like_count`            INT             NOT NULL DEFAULT 0 COMMENT '点赞数(异步回写)',
    `comment_count`         INT             NOT NULL DEFAULT 0 COMMENT '评论数(异步回写)',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    `created_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `created_at`),
    KEY `idx_author_created` (`author_id`, `created_at` DESC),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at` DESC),
    KEY `idx_play_session_id` (`play_session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态帖子'
PARTITION BY RANGE (TO_DAYS(`created_at`)) (
    PARTITION p2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
    PARTITION p2027q1 VALUES LESS THAN (TO_DAYS('2027-04-01')),
    PARTITION p2027q2 VALUES LESS THAN (TO_DAYS('2027-07-01')),
    PARTITION p2027q3 VALUES LESS THAN (TO_DAYS('2027-10-01')),
    PARTITION p2027q4 VALUES LESS THAN (TO_DAYS('2028-01-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- -----------------------------------------------------------
-- 2. feed_comments - 帖子评论 (按季度分区)
-- -----------------------------------------------------------
CREATE TABLE `feed_comments` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `post_id`               CHAR(36)        NOT NULL COMMENT '关联feed_posts.id',
    `author_id`             CHAR(36)        NOT NULL COMMENT '评论者ID',
    `content`               VARCHAR(300)    NOT NULL COMMENT '评论内容(≤300字)',
    `parent_comment_id`     CHAR(36)        NULL     COMMENT '父评论ID(支持嵌套)',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    `created_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `created_at`),
    KEY `idx_post_created` (`post_id`, `created_at`),
    KEY `idx_status` (`status`),
    KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子评论'
PARTITION BY RANGE (TO_DAYS(`created_at`)) (
    PARTITION p2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
    PARTITION p2027q1 VALUES LESS THAN (TO_DAYS('2027-04-01')),
    PARTITION p2027q2 VALUES LESS THAN (TO_DAYS('2027-07-01')),
    PARTITION p2027q3 VALUES LESS THAN (TO_DAYS('2027-10-01')),
    PARTITION p2027q4 VALUES LESS THAN (TO_DAYS('2028-01-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- -----------------------------------------------------------
-- 3. feed_likes - 点赞 (按季度分区)
-- -----------------------------------------------------------
CREATE TABLE `feed_likes` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `post_id`               CHAR(36)        NOT NULL COMMENT '关联feed_posts.id',
    `user_id`               CHAR(36)        NOT NULL COMMENT '点赞用户ID',
    `created_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`, `created_at`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`, `created_at`),
    KEY `idx_status` (`status`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞'
PARTITION BY RANGE (TO_DAYS(`created_at`)) (
    PARTITION p2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
    PARTITION p2027q1 VALUES LESS THAN (TO_DAYS('2027-04-01')),
    PARTITION p2027q2 VALUES LESS THAN (TO_DAYS('2027-07-01')),
    PARTITION p2027q3 VALUES LESS THAN (TO_DAYS('2027-10-01')),
    PARTITION p2027q4 VALUES LESS THAN (TO_DAYS('2028-01-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- -----------------------------------------------------------
-- 4. feed_media - 动态媒体
-- -----------------------------------------------------------
CREATE TABLE `feed_media` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `post_id`               CHAR(36)        NOT NULL COMMENT '关联feed_posts.id',
    `media_url`             VARCHAR(512)    NOT NULL COMMENT 'OSS URL',
    `media_type`            ENUM('image','video') NOT NULL COMMENT '媒体类型',
    `display_order`         TINYINT         NOT NULL DEFAULT 0 COMMENT '0-8排序',
    `thumbnail_url`         VARCHAR(512)    NULL     COMMENT 'OSS缩略图URL',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`, `display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态媒体';
