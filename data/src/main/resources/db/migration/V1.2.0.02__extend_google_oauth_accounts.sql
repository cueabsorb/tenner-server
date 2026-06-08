-- ============================================================
-- IRallyIn: Google OAuth account metadata
-- ============================================================

USE `ir_auth`;

ALTER TABLE `linked_accounts`
    ADD COLUMN `provider_email_verified` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '第三方邮箱是否已验证' AFTER `provider_email`,
    ADD COLUMN `provider_locale` VARCHAR(20) NULL COMMENT '第三方账号语言区域' AFTER `provider_avatar_url`,
    ADD COLUMN `provider_scope` VARCHAR(1000) NULL COMMENT '最近一次授权scope' AFTER `provider_locale`,
    ADD COLUMN `provider_link_updated_at` TIMESTAMP NULL COMMENT '第三方资料最近更新时间' AFTER `provider_scope`;

CREATE INDEX `idx_provider_email` ON `linked_accounts` (`provider`, `provider_email`);
