DELIMITER $$

DROP PROCEDURE IF EXISTS `ir_profile`.`add_permission_settings_column_if_missing`$$

CREATE PROCEDURE `ir_profile`.`add_permission_settings_column_if_missing`(
    IN column_name_value VARCHAR(64),
    IN column_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM `information_schema`.`COLUMNS`
        WHERE `TABLE_SCHEMA` = 'ir_profile'
          AND `TABLE_NAME` = 'user_profile_permission_settings'
          AND `COLUMN_NAME` = column_name_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `ir_profile`.`user_profile_permission_settings` ADD COLUMN ', column_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL `ir_profile`.`add_permission_settings_column_if_missing`('following_list_visible', '`following_list_visible` TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''搭子列表是否显示'' AFTER `habit_courts_visible`');
CALL `ir_profile`.`add_permission_settings_column_if_missing`('follower_list_visible', '`follower_list_visible` TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''粉丝列表是否显示'' AFTER `following_list_visible`');

DROP PROCEDURE `ir_profile`.`add_permission_settings_column_if_missing`;
