CREATE TABLE IF NOT EXISTS `ir_profile`.`user_profile_permission_settings` (
    `id`                       CHAR(36)     NOT NULL COMMENT '主键ID',
    `user_id`                  CHAR(36)     NOT NULL COMMENT '用户ID',
    `gender_visible`           TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '性别是否显示',
    `birthday_visible`         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '生日是否显示',
    `region_visible`           TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '区域设置是否显示',
    `habit_courts_visible`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '经常去的球场是否显示',
    `status`                   TINYINT      NOT NULL DEFAULT 0 COMMENT '0正常 1删除',
    `created_at`               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_profile_permission_user` (`user_id`),
    KEY `idx_user_profile_permission_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户主页权限设置';
