-- ============================================================
-- IRallyIn: ir_activity schema - 运动历史与关系网络
-- Target: PolarDB MySQL 8.0+
-- play_sessions 按季度分区
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_activity` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_activity`;

-- -----------------------------------------------------------
-- 1. play_sessions - 运动记录 (按季度分区)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `play_sessions` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `owner_id`              CHAR(36)        NOT NULL COMMENT '记录创建者ID',
    `sport_type`            VARCHAR(20)     NOT NULL DEFAULT 'tennis' COMMENT '运动类型',
    `session_type`          ENUM('rally','training','match','practiceMatch','other') NOT NULL COMMENT '场次类型',
    `title`                 VARCHAR(100)    NULL     COMMENT '标题',
    `started_at`            DATETIME        NOT NULL COMMENT '开始时间',
    `ended_at`              DATETIME        NULL     COMMENT '结束时间',
    `duration_minutes`      SMALLINT        NULL     COMMENT '持续分钟数',
    `city_code`             VARCHAR(10)     NULL     COMMENT '城市编码',
    `district_code`         VARCHAR(10)     NULL     COMMENT '区域编码',
    `court_id`              CHAR(36)        NULL     COMMENT '球场ID',
    `court_name`            VARCHAR(200)    NULL     COMMENT '球场名称(冗余)',
    `score_summary`         JSON            NULL     COMMENT '比赛比分详情',
    `notes`                 TEXT            NULL     COMMENT '备注',
    `privacy_level`         ENUM('nobody','matchedPlayers','all') NOT NULL DEFAULT 'matchedPlayers' COMMENT '隐私级别',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `started_at`),
    KEY `idx_owner_started` (`owner_id`, `started_at` DESC),
    KEY `idx_status` (`status`),
    KEY `idx_started_at` (`started_at` DESC),
    KEY `idx_court_id` (`court_id`, `started_at` DESC),
    KEY `idx_city_code` (`city_code`, `started_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动记录'
PARTITION BY RANGE (TO_DAYS(`started_at`)) (
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
-- 2. play_participants - 运动参与者
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `play_participants` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `session_id`            CHAR(36)        NOT NULL COMMENT '关联play_sessions.id',
    `user_id`               CHAR(36)        NULL     COMMENT '参与用户ID(NULL=未注册)',
    `display_name`          VARCHAR(50)     NOT NULL COMMENT '显示名称',
    `role`                  ENUM('owner','opponent','partner','coach','other') NOT NULL COMMENT '角色',
    `side`                  ENUM('side_a','side_b') NULL     COMMENT '比赛阵营',
    `ntrp_snapshot`         DECIMAL(3,1)    NULL     COMMENT '当时NTRP等级快照',
    `participant_status`    ENUM('confirmed','pending','rejected') NOT NULL DEFAULT 'pending' COMMENT '确认状态',
    `confirmed_at`          TIMESTAMP       NULL     COMMENT '确认时间',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`, `participant_status`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动参与者';

-- -----------------------------------------------------------
-- 3. player_relationship_edges - 球友关系边 (有向)
-- 每对用户两行: A→B 和 B→A
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `player_relationship_edges` (
    `id`                        CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`                   CHAR(36)        NOT NULL COMMENT '用户ID',
    `peer_user_id`              CHAR(36)        NOT NULL COMMENT '球友ID',
    `total_sessions`            INT             NOT NULL DEFAULT 0 COMMENT '共同打球次数',
    `last_played_at`            TIMESTAMP       NULL     COMMENT '最后一起打球时间',
    `most_common_session_type`  ENUM('rally','training','match','practiceMatch','other') NULL COMMENT '最常见场次类型',
    `relationship_strength`     DECIMAL(5,2)    NOT NULL DEFAULT 0.00 COMMENT '关系强度(0-100)',
    `updated_at`                TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                    TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_peer` (`user_id`, `peer_user_id`),
    KEY `idx_peer_user` (`peer_user_id`),
    KEY `idx_strength` (`user_id`, `relationship_strength` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球友关系边';
