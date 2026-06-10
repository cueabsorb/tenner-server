CREATE TABLE IF NOT EXISTS `ir_profile`.`court_likes` (
    `id`          CHAR(36)    NOT NULL COMMENT 'UUID v4',
    `court_id`    CHAR(36)    NOT NULL COMMENT '关联ir_profile.courts.id',
    `user_id`     CHAR(36)    NOT NULL COMMENT '点赞用户ir_auth.users.id',
    `created_at`  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `status`      TINYINT     NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_court_likes_court_user` (`court_id`, `user_id`),
    KEY `idx_court_likes_court` (`court_id`),
    KEY `idx_court_likes_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网球场点赞记录';
