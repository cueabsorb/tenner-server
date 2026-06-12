ALTER TABLE `ir_club`.`club_events`
    MODIFY COLUMN `club_id` CHAR(36) NULL COMMENT '关联clubs.id；个人约球可为空';
