-- IRallyIn: require court location and owner fields.
-- Required fields: id, name, country, province, city, created_by. Other descriptive fields may be empty.
ALTER TABLE `ir_profile`.`courts`
    ADD COLUMN `province` VARCHAR(100) NULL COMMENT '省/州' AFTER `country`;

ALTER TABLE `ir_profile`.`court_change_requests`
    ADD COLUMN `province` VARCHAR(100) NULL COMMENT '省/州' AFTER `country`;

UPDATE `ir_profile`.`courts`
SET `country` = '未设置'
WHERE `country` IS NULL OR TRIM(`country`) = '';

UPDATE `ir_profile`.`courts`
SET `province` = '未设置'
WHERE `province` IS NULL OR TRIM(`province`) = '';

UPDATE `ir_profile`.`courts`
SET `city` = '未设置'
WHERE `city` IS NULL OR TRIM(`city`) = '';

UPDATE `ir_profile`.`courts`
SET `created_by` = '00000000-0000-0000-0000-000000000000'
WHERE `created_by` IS NULL OR TRIM(`created_by`) = '';

UPDATE `ir_profile`.`court_change_requests`
SET `country` = '未设置'
WHERE `country` IS NULL OR TRIM(`country`) = '';

UPDATE `ir_profile`.`court_change_requests`
SET `province` = '未设置'
WHERE `province` IS NULL OR TRIM(`province`) = '';

UPDATE `ir_profile`.`court_change_requests`
SET `city` = '未设置'
WHERE `city` IS NULL OR TRIM(`city`) = '';

ALTER TABLE `ir_profile`.`courts`
    MODIFY COLUMN `country` VARCHAR(100) NOT NULL COMMENT '国家/地区',
    MODIFY COLUMN `province` VARCHAR(100) NOT NULL COMMENT '省/州',
    MODIFY COLUMN `city` VARCHAR(100) NOT NULL COMMENT '城市',
    MODIFY COLUMN `created_by` CHAR(36) NOT NULL COMMENT '提交该球场的用户ID';

ALTER TABLE `ir_profile`.`court_change_requests`
    MODIFY COLUMN `country` VARCHAR(100) NOT NULL COMMENT '国家/地区',
    MODIFY COLUMN `province` VARCHAR(100) NOT NULL COMMENT '省/州',
    MODIFY COLUMN `city` VARCHAR(100) NOT NULL COMMENT '城市';
