-- ============================================================
-- IRallyIn: ir_review schema - 球员评价
-- Target: PolarDB MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_review` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_review`;

-- -----------------------------------------------------------
-- 1. player_reviews - 球员评价 (追加型, 软删除)
-- 防刷: UNIQUE(reviewer_id, play_session_id, reviewee_id)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_reviews` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `play_session_id`       CHAR(36)        NULL     COMMENT '关联运动记录ID(NULL=无共同记录)',
    `reviewer_id`           CHAR(36)        NOT NULL COMMENT '评价者ID',
    `reviewee_id`           CHAR(36)        NOT NULL COMMENT '被评价者ID',
    `skill_score`           TINYINT         NOT NULL COMMENT '球技评分(1-5)',
    `sportsmanship_score`   TINYINT         NOT NULL COMMENT '体育精神评分(1-5)',
    `reliability_score`     TINYINT         NOT NULL COMMENT '守约可靠评分(1-5)',
    `communication_score`   TINYINT         NOT NULL COMMENT '沟通友善评分(1-5)',
    `overall_score`         DECIMAL(3,1)    NOT NULL COMMENT '加权总分',
    `tags`                  JSON            NULL     COMMENT '评价标签数组',
    `private_note`          TEXT            NULL     COMMENT '私密备注(仅评价者可见)',
    `moderation_status`     ENUM('active','flagged','hidden') NOT NULL DEFAULT 'active' COMMENT '内容审核状态',
    `is_verified`           TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否绑定已确认运动记录',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_reviewer_session_reviewee` (`reviewer_id`, `play_session_id`, `reviewee_id`),
    KEY `idx_reviewee_created` (`reviewee_id`, `created_at` DESC),
    KEY `idx_reviewer_created` (`reviewer_id`, `created_at` DESC),
    KEY `idx_moderation_status` (`moderation_status`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球员评价';

-- -----------------------------------------------------------
-- 2. player_review_aggregates - 评价聚合 (每用户一行, 异步计算)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_review_aggregates` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '被评价用户ID',
    `total_review_count`    INT             NOT NULL DEFAULT 0 COMMENT '总评价数',
    `overall_score`         DECIMAL(3,2)    NOT NULL DEFAULT 0.00 COMMENT '总评分',
    `skill_avg`             DECIMAL(3,2)    NOT NULL DEFAULT 0.00 COMMENT '球技均分',
    `sportsmanship_avg`     DECIMAL(3,2)    NOT NULL DEFAULT 0.00 COMMENT '体育精神均分',
    `reliability_avg`       DECIMAL(3,2)    NOT NULL DEFAULT 0.00 COMMENT '守约可靠均分',
    `communication_avg`     DECIMAL(3,2)    NOT NULL DEFAULT 0.00 COMMENT '沟通友善均分',
    `top_positive_tags`     JSON            NULL     COMMENT '前5正面标签及计数',
    `trust_confidence`      DECIMAL(5,4)    NOT NULL DEFAULT 0.0000 COMMENT '信任置信度(0-1)',
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_overall_score` (`overall_score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价聚合';

-- -----------------------------------------------------------
-- 3. review_risk_logs - 评价风控日志 (追加型)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `review_risk_logs` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `review_id`             CHAR(36)        NOT NULL COMMENT '关联player_reviews.id',
    `risk_type`             VARCHAR(30)     NOT NULL COMMENT '风险类型: extreme_score/high_frequency/mutual_inflation',
    `risk_detail`           JSON            NULL     COMMENT '结构化风险详情',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_review_id` (`review_id`),
    KEY `idx_risk_type` (`risk_type`, `created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价风控日志';
