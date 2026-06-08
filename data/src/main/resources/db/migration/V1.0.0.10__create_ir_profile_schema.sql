-- ============================================================
-- IRallyIn: ir_profile schema - 用户档案
-- Target: PolarDB MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_profile` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_profile`;

-- -----------------------------------------------------------
-- 1. player_skill_profiles - 技能档案 (每用户一行)
-- -----------------------------------------------------------
CREATE TABLE `player_skill_profiles` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联ir_auth.users.id',
    `ntrp_rating`           DECIMAL(3,1)    NOT NULL COMMENT '1.0-5.5',
    `player_identity`       ENUM('amateur','professional','coach','coCoach') NOT NULL COMMENT '球员身份',
    `confidence_score`      DECIMAL(5,4)    NOT NULL DEFAULT 0.0000 COMMENT '0.0-1.0 置信度',
    `verified_level`        VARCHAR(20)     NULL     COMMENT 'self_assessed/coach_verified等',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_ntrp_rating` (`ntrp_rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能档案';

-- -----------------------------------------------------------
-- 2. skill_certifications - 认证记录
-- -----------------------------------------------------------
CREATE TABLE `skill_certifications` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `cert_type`             ENUM('selfAssessment','photoUpload','videoUpload','thirdPartyCert','bigDataRating','peerReview') NOT NULL COMMENT '认证类型',
    `cert_status`           ENUM('draft','pending','verified','rejected','expired') NOT NULL DEFAULT 'draft' COMMENT '认证状态',
    `requested_ntrp`        DECIMAL(3,1)    NULL     COMMENT '用户自报等级',
    `final_ntrp`            DECIMAL(3,1)    NULL     COMMENT '认证后等级',
    `player_identity`       ENUM('amateur','professional','coach','coCoach') NULL COMMENT '球员身份',
    `coach_id`              CHAR(36)        NULL     COMMENT '第三方认证的教练ID',
    `coach_comment`         TEXT            NULL     COMMENT '教练评语(500字)',
    `video_analysis_result` JSON            NULL     COMMENT 'V2 AI视频分析结果',
    `big_data_score`        JSON            NULL     COMMENT '大数据评分详情',
    `submitted_at`          TIMESTAMP       NULL     COMMENT '提交时间',
    `verified_at`           TIMESTAMP       NULL     COMMENT '认证通过时间',
    `expired_at`            TIMESTAMP       NULL     COMMENT '过期时间',
    `rejected_reason`       VARCHAR(500)    NULL     COMMENT '拒绝原因',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id_cert_status` (`user_id`, `cert_status`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_cert_status_submitted` (`cert_status`, `submitted_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认证记录';

-- -----------------------------------------------------------
-- 3. cert_photos - 认证照片 (1-5张/认证)
-- -----------------------------------------------------------
CREATE TABLE `cert_photos` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `certification_id`      CHAR(36)        NOT NULL COMMENT '关联skill_certifications.id',
    `photo_url`             VARCHAR(512)    NOT NULL COMMENT 'OSS URL',
    `thumbnail_url`         VARCHAR(512)    NULL     COMMENT 'OSS缩略图URL',
    `display_order`         TINYINT         NOT NULL DEFAULT 0 COMMENT '0-4排序',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_certification_id` (`certification_id`, `display_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认证照片';

-- -----------------------------------------------------------
-- 4. skill_videos - 认证视频 (1个/认证)
-- -----------------------------------------------------------
CREATE TABLE `skill_videos` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `certification_id`      CHAR(36)        NOT NULL COMMENT '关联skill_certifications.id (唯一)',
    `video_url`             VARCHAR(512)    NOT NULL COMMENT 'OSS URL',
    `thumbnail_url`         VARCHAR(512)    NULL     COMMENT 'OSS缩略图',
    `duration_seconds`      SMALLINT        NULL     COMMENT '时长秒数(≤60)',
    `file_size_mb`          DECIMAL(6,1)    NULL     COMMENT '文件大小MB',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_certification_id` (`certification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认证视频';

-- -----------------------------------------------------------
-- 5. playing_habits - 打球习惯 (每用户一行)
-- -----------------------------------------------------------
CREATE TABLE `playing_habits` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `monthly_freq_min`      TINYINT         NULL     COMMENT '每月最低打球次数',
    `monthly_freq_max`      TINYINT         NULL     COMMENT '每月最高打球次数',
    `court_surface_pref`    SET('hard','clay','grass','carpet') NULL COMMENT '偏好场地类型(多选)',
    `indoor_outdoor_pref`   ENUM('indoor','outdoor','both') NULL COMMENT '室内外偏好',
    `play_preference`       ENUM('singles','doubles','both') NULL COMMENT '单双打偏好',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打球习惯';

-- -----------------------------------------------------------
-- 6. habit_cities - 习惯关联城市 (多对多)
-- -----------------------------------------------------------
CREATE TABLE `habit_cities` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `habit_id`              CHAR(36)        NOT NULL COMMENT '关联playing_habits.id',
    `city_code`             VARCHAR(10)     NOT NULL COMMENT '关联cities.code',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_habit_city` (`habit_id`, `city_code`),
    KEY `idx_city_code` (`city_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='习惯关联城市';

-- -----------------------------------------------------------
-- 7. habit_districts - 习惯关联区域
-- -----------------------------------------------------------
CREATE TABLE `habit_districts` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `habit_id`              CHAR(36)        NOT NULL COMMENT '关联playing_habits.id',
    `district_code`         VARCHAR(10)     NOT NULL COMMENT '关联areas.code',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_habit_district` (`habit_id`, `district_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='习惯关联区域';

-- -----------------------------------------------------------
-- 8. habit_courts - 习惯关联球场
-- -----------------------------------------------------------
CREATE TABLE `habit_courts` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `habit_id`              CHAR(36)        NOT NULL COMMENT '关联playing_habits.id',
    `court_id`              CHAR(36)        NOT NULL COMMENT '关联courts.id',
    `is_primary`            TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否首选球场(仅一个)',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_habit_court` (`habit_id`, `court_id`),
    KEY `idx_court_id` (`court_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='习惯关联球场';

-- -----------------------------------------------------------
-- 9. habit_weekly_slots - 周时间段
-- -----------------------------------------------------------
CREATE TABLE `habit_weekly_slots` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `habit_id`              CHAR(36)        NOT NULL COMMENT '关联playing_habits.id',
    `day_of_week`           TINYINT         NOT NULL COMMENT '1=周一 7=周日',
    `start_time`            TIME            NOT NULL COMMENT '开始时间(本地)',
    `end_time`              TIME            NOT NULL COMMENT '结束时间(本地)',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_habit_id` (`habit_id`, `day_of_week`),
    CONSTRAINT `chk_time_range` CHECK (`end_time` > `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每周打球时间段';

-- -----------------------------------------------------------
-- 10. schedule_overrides - 日程覆盖
-- -----------------------------------------------------------
CREATE TABLE `schedule_overrides` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `habit_id`              CHAR(36)        NOT NULL COMMENT '关联playing_habits.id',
    `date`                  DATE            NOT NULL COMMENT '具体日期',
    `is_available`          TINYINT(1)      NOT NULL COMMENT '是否可打球',
    `note`                  VARCHAR(200)    NULL     COMMENT '备注如"出差"',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_habit_date` (`habit_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程覆盖';

-- -----------------------------------------------------------
-- 11. tennis_profiles - 网球兴趣 (每用户一行)
-- -----------------------------------------------------------
CREATE TABLE `tennis_profiles` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `play_preference`       ENUM('singles','doubles','both') NULL COMMENT '单双打偏好',
    `backhand_type`         ENUM('oneHanded','twoHanded') NULL COMMENT '反手类型',
    `playing_style`         ENUM('baseliner','serveAndVolley','allCourt','counterPuncher') NULL COMMENT '打法风格',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网球兴趣档案';

-- -----------------------------------------------------------
-- 12. tennis_profile_idols - 偶像球星 (最多5个)
-- -----------------------------------------------------------
CREATE TABLE `tennis_profile_idols` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `profile_id`            CHAR(36)        NOT NULL COMMENT '关联tennis_profiles.id',
    `tennis_star_id`        CHAR(36)        NULL     COMMENT '关联tennis_stars.id',
    `custom_name`           VARCHAR(50)     NULL     COMMENT '自定义球星名称',
    `display_order`         TINYINT         NOT NULL DEFAULT 0 COMMENT '0-4排序',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_profile_id` (`profile_id`, `display_order`),
    CONSTRAINT `chk_idol_source` CHECK (`tennis_star_id` IS NOT NULL OR `custom_name` IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='偶像球星';

-- -----------------------------------------------------------
-- 13. tennis_profile_serve_types - 发球类型
-- -----------------------------------------------------------
CREATE TABLE `tennis_profile_serve_types` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `profile_id`            CHAR(36)        NOT NULL COMMENT '关联tennis_profiles.id',
    `serve_type`            ENUM('flat','kick','slice','varied') NOT NULL COMMENT '发球类型',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_profile_serve` (`profile_id`, `serve_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发球类型';

-- -----------------------------------------------------------
-- 14. tennis_profile_strength_tags - 优势标签
-- -----------------------------------------------------------
CREATE TABLE `tennis_profile_strength_tags` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `profile_id`            CHAR(36)        NOT NULL COMMENT '关联tennis_profiles.id',
    `tag`                   VARCHAR(30)     NOT NULL COMMENT '标签名',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_profile_tag` (`profile_id`, `tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优势标签';

-- -----------------------------------------------------------
-- 15. tennis_profile_improvement_tags - 待改进标签
-- -----------------------------------------------------------
CREATE TABLE `tennis_profile_improvement_tags` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `profile_id`            CHAR(36)        NOT NULL COMMENT '关联tennis_profiles.id',
    `tag`                   VARCHAR(30)     NOT NULL COMMENT '标签名',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_profile_tag` (`profile_id`, `tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待改进标签';

-- -----------------------------------------------------------
-- 16. equipment_bags - 装备包 (每用户一行)
-- -----------------------------------------------------------
CREATE TABLE `equipment_bags` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '关联用户ID',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='装备包';

-- -----------------------------------------------------------
-- 17. rackets - 球拍 (每用户最多20个)
-- -----------------------------------------------------------
CREATE TABLE `rackets` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `bag_id`                CHAR(36)        NOT NULL COMMENT '关联equipment_bags.id',
    `brand`                 VARCHAR(50)     NOT NULL COMMENT '品牌',
    `model`                 VARCHAR(100)    NOT NULL COMMENT '型号',
    `grip_size`             VARCHAR(10)     NULL     COMMENT '握把尺寸如4_3/8',
    `weight_gram`           SMALLINT        NULL     COMMENT '重量(克)',
    `head_size_sq_in`       SMALLINT        NULL     COMMENT '拍面大小(平方英寸)',
    `image_url`             VARCHAR(512)    NULL     COMMENT 'OSS URL',
    `is_primary`            TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否主用球拍',
    `display_order`         TINYINT         NOT NULL DEFAULT 0 COMMENT '排序',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_bag_id` (`bag_id`, `display_order`),
    KEY `idx_bag_primary` (`bag_id`, `is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球拍';

-- -----------------------------------------------------------
-- 18. tennis_shoes - 球鞋
-- -----------------------------------------------------------
CREATE TABLE `tennis_shoes` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `bag_id`                CHAR(36)        NOT NULL COMMENT '关联equipment_bags.id',
    `brand`                 VARCHAR(50)     NOT NULL COMMENT '品牌',
    `model`                 VARCHAR(100)    NOT NULL COMMENT '型号',
    `size`                  VARCHAR(10)     NULL     COMMENT '尺码',
    `court_type`            ENUM('hard','clay','grass','carpet','all') NULL COMMENT '适用场地',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_bag_id` (`bag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球鞋';

-- -----------------------------------------------------------
-- 19. tennis_string_setups - 穿线配置
-- -----------------------------------------------------------
CREATE TABLE `tennis_string_setups` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `racket_id`             CHAR(36)        NULL     COMMENT '关联rackets.id(可独立存在)',
    `main_string_brand`     VARCHAR(50)     NULL     COMMENT '竖线品牌',
    `cross_string_brand`    VARCHAR(50)     NULL     COMMENT '横线品牌',
    `string_type`           ENUM('naturalGut','multifilament','monofilament','hybrid') NULL COMMENT '线型',
    `main_tension_lbs`      DECIMAL(4,1)    NULL     COMMENT '竖线磅数(35-75)',
    `cross_tension_lbs`     DECIMAL(4,1)    NULL     COMMENT '横线磅数(35-75)',
    `strung_at`             DATE            NULL     COMMENT '穿线日期',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_racket_id` (`racket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='穿线配置';

-- -----------------------------------------------------------
-- 参考数据表
-- -----------------------------------------------------------

-- 20. cities - 城市
CREATE TABLE `cities` (
    `code`                  VARCHAR(10)     NOT NULL COMMENT '城市编码如SHA',
    `name`                  VARCHAR(50)     NOT NULL COMMENT '城市名',
    `english_name`          VARCHAR(50)     NULL     COMMENT '英文名',
    `country_code`          CHAR(2)         NOT NULL COMMENT 'ISO 3166-1 alpha-2',
    `latitude`              DECIMAL(9,6)    NULL     COMMENT '纬度',
    `longitude`             DECIMAL(9,6)    NULL     COMMENT '经度',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='城市';

-- 21. areas - 区域/区县
CREATE TABLE `areas` (
    `code`                  VARCHAR(10)     NOT NULL COMMENT '区域编码',
    `city_code`             VARCHAR(10)     NOT NULL COMMENT '关联cities.code',
    `name`                  VARCHAR(50)     NOT NULL COMMENT '区域名',
    `english_name`          VARCHAR(50)     NULL     COMMENT '英文名',
    `latitude`              DECIMAL(9,6)    NULL     COMMENT '纬度',
    `longitude`             DECIMAL(9,6)    NULL     COMMENT '经度',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`code`),
    KEY `idx_city_code` (`city_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='区域';

-- 22. courts - 球场
CREATE TABLE `courts` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `area_code`             VARCHAR(10)     NULL     COMMENT '关联areas.code',
    `name`                  VARCHAR(200)    NOT NULL COMMENT '球场名',
    `address`               VARCHAR(500)    NULL     COMMENT '地址',
    `latitude`              DECIMAL(9,6)    NULL     COMMENT '纬度',
    `longitude`             DECIMAL(9,6)    NULL     COMMENT '经度',
    `surface_type`          SET('hard','clay','grass','carpet') NULL COMMENT '地面类型',
    `indoor_outdoor`        ENUM('indoor','outdoor','both') NULL COMMENT '室内外',
    `contact_phone`         VARCHAR(20)     NULL     COMMENT '联系电话',
    `amenities`             JSON            NULL     COMMENT '设施如["parking","locker_room"]',
    `venue_status`          ENUM('active','inactive','pending_review') NOT NULL DEFAULT 'active' COMMENT '场地运营状态',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_area_code` (`area_code`),
    KEY `idx_lat_lng` (`latitude`, `longitude`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球场';

-- 23. tennis_stars - 网球明星
CREATE TABLE `tennis_stars` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `name`                  VARCHAR(100)    NOT NULL COMMENT '姓名',
    `country`               CHAR(2)         NULL     COMMENT 'ISO 3166-1国籍',
    `avatar_url`            VARCHAR(512)    NULL     COMMENT 'OSS头像URL',
    `active_status`         ENUM('active','retired') NOT NULL DEFAULT 'active' COMMENT '活跃状态',
    `handedness`            ENUM('right','left') NULL COMMENT '持拍手',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网球明星';
