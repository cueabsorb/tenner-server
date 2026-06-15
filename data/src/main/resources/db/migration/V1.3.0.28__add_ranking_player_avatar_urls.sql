ALTER TABLE `ir_profile`.`world_ranking_players`
    ADD COLUMN IF NOT EXISTS `avatar_url` VARCHAR(512) NULL COMMENT '头像URL，NULL或空字符串时客户端使用App默认logo'
    AFTER `source`;

ALTER TABLE `ir_profile`.`city_ranking_players`
    ADD COLUMN IF NOT EXISTS `avatar_url` VARCHAR(512) NULL COMMENT '头像URL，NULL或空字符串时客户端使用App默认logo'
    AFTER `group_wins`;
