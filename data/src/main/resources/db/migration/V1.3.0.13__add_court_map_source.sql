ALTER TABLE `ir_profile`.`courts`
    ADD COLUMN `map_source` VARCHAR(40) NULL COMMENT '地图来源，如 appleMap' AFTER `longitude`;

ALTER TABLE `ir_profile`.`court_change_requests`
    ADD COLUMN `map_source` VARCHAR(40) NULL COMMENT '地图来源，如 appleMap' AFTER `longitude`;
