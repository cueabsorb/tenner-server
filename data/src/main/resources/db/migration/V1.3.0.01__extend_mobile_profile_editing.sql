-- ============================================================
-- IRallyIn: mobile profile editing extensions
-- Target: PolarDB MySQL 8.0+
-- ============================================================

ALTER TABLE `ir_auth`.`users`
    ADD COLUMN `bio` VARCHAR(160) NULL COMMENT '个人主页简介' AFTER `avatar_url`,
    ADD COLUMN `gender` ENUM('male','female') NULL COMMENT '性别' AFTER `bio`,
    ADD COLUMN `birthday` DATE NULL COMMENT '生日' AFTER `gender`,
    ADD COLUMN `province` VARCHAR(100) NULL COMMENT '省/州' AFTER `country`,
    ADD COLUMN `district` VARCHAR(100) NULL COMMENT '区/县' AFTER `city`,
    ADD COLUMN `real_name_visible` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否展示实名信息' AFTER `district`,
    ADD COLUMN `dominant_hand` ENUM('left','right') NULL COMMENT '常用手' AFTER `sys_ntrp_rating`;

CREATE TABLE IF NOT EXISTS `ir_profile`.`profile_edit_audit_logs` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `field_name`            VARCHAR(40)     NOT NULL COMMENT '编辑字段',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_field_created` (`user_id`, `field_name`, `created_at` DESC),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='个人主页编辑频率审计';

ALTER TABLE `ir_auth`.`users`
    MODIFY COLUMN `ntrp_rating` DECIMAL(3,1) NULL COMMENT '用户自评网球水平 0.5-7.0',
    MODIFY COLUMN `sys_ntrp_rating` DECIMAL(3,1) NULL COMMENT '系统根据历史数据计算的网球水平 0.5-7.0';

ALTER TABLE `ir_profile`.`player_skill_profiles`
    MODIFY COLUMN `ntrp_rating` DECIMAL(3,1) NOT NULL COMMENT '0.5-7.0';
