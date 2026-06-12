USE `ir_activity`;

DROP PROCEDURE IF EXISTS `add_play_sessions_column_if_missing`;
DELIMITER $$
CREATE PROCEDURE `add_play_sessions_column_if_missing`(
    IN p_column_name VARCHAR(64),
    IN p_column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = 'ir_activity'
          AND TABLE_NAME = 'play_sessions'
          AND COLUMN_NAME = p_column_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `ir_activity`.`play_sessions` ADD COLUMN ', p_column_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

CALL `add_play_sessions_column_if_missing`(
    'healthkit_uuid',
    '`healthkit_uuid` VARCHAR(80) NULL COMMENT ''来源 HealthKit HKWorkout UUID'' AFTER `privacy_level`'
);

DROP PROCEDURE IF EXISTS `add_play_sessions_index_if_missing`;
DELIMITER $$
CREATE PROCEDURE `add_play_sessions_index_if_missing`(
    IN p_index_name VARCHAR(64),
    IN p_index_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = 'ir_activity'
          AND TABLE_NAME = 'play_sessions'
          AND INDEX_NAME = p_index_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `ir_activity`.`play_sessions` ADD INDEX ', p_index_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

CALL `add_play_sessions_index_if_missing`(
    'idx_owner_healthkit',
    '`idx_owner_healthkit` (`owner_id`, `healthkit_uuid`, `started_at` DESC)'
);

DROP PROCEDURE IF EXISTS `add_play_sessions_column_if_missing`;
DROP PROCEDURE IF EXISTS `add_play_sessions_index_if_missing`;
