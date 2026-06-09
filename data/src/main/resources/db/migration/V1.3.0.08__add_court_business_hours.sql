ALTER TABLE `ir_profile`.`courts`
    ADD COLUMN `opening_time` TIME NULL COMMENT '每日营业开始时间' AFTER `outdoor_court_count`,
    ADD COLUMN `closing_time` TIME NULL COMMENT '每日营业结束时间' AFTER `opening_time`;
