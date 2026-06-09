-- IRallyIn: relax optional court fields.
-- Court id and name are required; other descriptive fields may be empty.
ALTER TABLE `ir_profile`.`courts`
    MODIFY COLUMN `has_indoor` TINYINT(1) NULL COMMENT '是否有室内场地',
    MODIFY COLUMN `has_outdoor` TINYINT(1) NULL COMMENT '是否有室外场地';

ALTER TABLE `ir_profile`.`court_change_requests`
    MODIFY COLUMN `country` VARCHAR(100) NULL COMMENT '国家/地区',
    MODIFY COLUMN `city` VARCHAR(100) NULL COMMENT '城市',
    MODIFY COLUMN `has_indoor` TINYINT(1) NULL COMMENT '申请修改后是否有室内场地',
    MODIFY COLUMN `has_outdoor` TINYINT(1) NULL COMMENT '申请修改后是否有室外场地';
