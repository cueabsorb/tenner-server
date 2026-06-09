ALTER TABLE `ir_profile`.`courts`
    MODIFY COLUMN `contact_phone` VARCHAR(40) NULL COMMENT '联系电话',
    MODIFY COLUMN `wechat_mini_program_name` VARCHAR(100) NULL COMMENT '微信小程序名称';
