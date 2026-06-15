ALTER TABLE `ir_profile`.`world_ranking_players`
    MODIFY COLUMN `gender` ENUM('men','women','woman') NOT NULL COMMENT '男子/女子';

UPDATE `ir_profile`.`world_ranking_players`
SET `gender` = 'woman'
WHERE `gender` = 'women';

ALTER TABLE `ir_profile`.`world_ranking_players`
    MODIFY COLUMN `gender` ENUM('men','woman') NOT NULL COMMENT '男子/女子';
