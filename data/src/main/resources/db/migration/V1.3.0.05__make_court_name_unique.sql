-- ============================================================
-- IRallyIn: make court name unique
-- Uses CourtDO.name as the unique identifier for courts.
-- ============================================================

ALTER TABLE `ir_profile`.`courts`
    DROP INDEX `idx_name`,
    ADD UNIQUE KEY `uk_courts_name` (`name`);
