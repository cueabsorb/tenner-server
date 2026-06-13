ALTER TABLE `ir_profile`.`world_ranking_players`
    ADD COLUMN IF NOT EXISTS `ntrp` DECIMAL(3,1) NOT NULL DEFAULT 7.0 COMMENT 'NTRP评级，职业球员默认7.0'
    AFTER `points`;
