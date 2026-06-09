-- ============================================================
-- IRallyIn: extend court facility markers
-- Adds multi-surface and indoor/outdoor court count markers.
-- ============================================================

ALTER TABLE `ir_profile`.`courts`
    MODIFY COLUMN `surface_type` SET('hard','clay','grass','sand_grass','carpet') NULL COMMENT '地面类型，可多选: hard=硬地, clay=红土, grass=草地, sand_grass=沙草, carpet=地毯',
    ADD COLUMN `has_indoor` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有室内场地' AFTER `indoor_outdoor`,
    ADD COLUMN `has_outdoor` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否有室外场地' AFTER `has_indoor`,
    ADD COLUMN `total_court_count` INT NULL COMMENT '球场总片数' AFTER `has_outdoor`,
    ADD COLUMN `indoor_court_count` INT NULL COMMENT '室内球场片数' AFTER `total_court_count`,
    ADD COLUMN `outdoor_court_count` INT NULL COMMENT '室外球场片数' AFTER `indoor_court_count`;
