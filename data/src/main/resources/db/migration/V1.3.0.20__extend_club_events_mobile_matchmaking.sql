DELIMITER $$

DROP PROCEDURE IF EXISTS `ir_club`.`add_club_events_column_if_missing`$$

CREATE PROCEDURE `ir_club`.`add_club_events_column_if_missing`(
    IN column_name_value VARCHAR(64),
    IN column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM `information_schema`.`COLUMNS`
        WHERE `TABLE_SCHEMA` = 'ir_club'
          AND `TABLE_NAME` = 'club_events'
          AND `COLUMN_NAME` = column_name_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `ir_club`.`club_events` ADD COLUMN ', column_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `ir_club`.`add_club_events_index_if_missing`$$

CREATE PROCEDURE `ir_club`.`add_club_events_index_if_missing`(
    IN index_name_value VARCHAR(64),
    IN index_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM `information_schema`.`STATISTICS`
        WHERE `TABLE_SCHEMA` = 'ir_club'
          AND `TABLE_NAME` = 'club_events'
          AND `INDEX_NAME` = index_name_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `ir_club`.`club_events` ADD INDEX ', index_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL `ir_club`.`add_club_events_column_if_missing`('country', '`country` VARCHAR(64) NULL COMMENT ''国家/地区'' AFTER `court_name`');
CALL `ir_club`.`add_club_events_column_if_missing`('province', '`province` VARCHAR(64) NULL COMMENT ''省/州'' AFTER `country`');
CALL `ir_club`.`add_club_events_column_if_missing`('city', '`city` VARCHAR(64) NULL COMMENT ''城市'' AFTER `province`');
CALL `ir_club`.`add_club_events_column_if_missing`('district', '`district` VARCHAR(64) NULL COMMENT ''区县'' AFTER `city`');
CALL `ir_club`.`add_club_events_column_if_missing`('match_type', '`match_type` VARCHAR(20) NULL COMMENT ''约球类型'' AFTER `ended_at`');
CALL `ir_club`.`add_club_events_column_if_missing`('needed_players', '`needed_players` SMALLINT NULL COMMENT ''还缺人数'' AFTER `match_type`');
CALL `ir_club`.`add_club_events_column_if_missing`('min_level', '`min_level` DECIMAL(3,1) NULL COMMENT ''最低NTRP'' AFTER `needed_players`');
CALL `ir_club`.`add_club_events_column_if_missing`('max_level', '`max_level` DECIMAL(3,1) NULL COMMENT ''最高NTRP'' AFTER `min_level`');
CALL `ir_club`.`add_club_events_column_if_missing`('price_mode', '`price_mode` VARCHAR(20) NULL COMMENT ''费用模式'' AFTER `max_level`');
CALL `ir_club`.`add_club_events_column_if_missing`('price_per_person', '`price_per_person` INT NULL COMMENT ''每人费用'' AFTER `price_mode`');
CALL `ir_club`.`add_club_events_column_if_missing`('gender_requirement', '`gender_requirement` VARCHAR(20) NULL COMMENT ''性别要求'' AFTER `price_per_person`');
CALL `ir_club`.`add_club_events_column_if_missing`('note', '`note` VARCHAR(300) NULL COMMENT ''约球备注'' AFTER `gender_requirement`');
CALL `ir_club`.`add_club_events_index_if_missing`('idx_mobile_region_started', '`idx_mobile_region_started` (`country`, `province`, `city`, `event_status`, `started_at`)');

DROP PROCEDURE `ir_club`.`add_club_events_column_if_missing`;
DROP PROCEDURE `ir_club`.`add_club_events_index_if_missing`;
