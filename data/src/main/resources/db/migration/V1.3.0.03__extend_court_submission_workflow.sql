-- ============================================================
-- IRallyIn: extend court submission workflow
-- Adds user-submitted court metadata and approval lifecycle.
-- ============================================================

ALTER TABLE `ir_profile`.`courts`
    ADD COLUMN `country` VARCHAR(100) NULL COMMENT '国家/地区' AFTER `id`,
    ADD COLUMN `city` VARCHAR(100) NULL COMMENT '城市' AFTER `country`,
    MODIFY COLUMN `surface_type` SET('hard','clay','grass','sand_grass','carpet') NULL COMMENT '地面类型，可多选: hard=硬地, clay=红土, grass=草地, sand_grass=沙草, carpet=地毯',
    ADD COLUMN `has_indoor` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有室内场地' AFTER `indoor_outdoor`,
    ADD COLUMN `has_outdoor` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有室外场地' AFTER `has_indoor`,
    ADD COLUMN `total_court_count` INT NULL COMMENT '球场总片数' AFTER `has_outdoor`,
    ADD COLUMN `indoor_court_count` INT NULL COMMENT '室内球场片数' AFTER `total_court_count`,
    ADD COLUMN `outdoor_court_count` INT NULL COMMENT '室外球场片数' AFTER `indoor_court_count`,
    ADD COLUMN `wechat_mini_program_name` VARCHAR(100) NULL COMMENT '微信小程序名称' AFTER `contact_phone`,
    ADD COLUMN `photo_urls` JSON NULL COMMENT '球场照片URL数组，建议3-5张' AFTER `wechat_mini_program_name`,
    ADD COLUMN `description` TEXT NULL COMMENT '球场补充信息描述' AFTER `photo_urls`,
    ADD COLUMN `created_by` CHAR(36) NULL COMMENT '提交该球场的用户ID' AFTER `description`,
    ADD COLUMN `approval_status` ENUM('pending','approved','rejected') NOT NULL DEFAULT 'approved' COMMENT '平台审核状态' AFTER `venue_status`,
    ADD COLUMN `operator_managed` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否场馆主动运营' AFTER `approval_status`,
    ADD COLUMN `reviewed_by` CHAR(36) NULL COMMENT '审核人ID' AFTER `operator_managed`,
    ADD COLUMN `reviewed_at` DATETIME NULL COMMENT '审核时间' AFTER `reviewed_by`,
    ADD COLUMN `rejected_reason` VARCHAR(500) NULL COMMENT '拒绝原因' AFTER `reviewed_at`,
    ADD KEY `idx_country_city_name` (`country`, `city`, `name`),
    ADD KEY `idx_created_by` (`created_by`),
    ADD KEY `idx_approval_status` (`approval_status`);
