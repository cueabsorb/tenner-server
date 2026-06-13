-- =========================================================
-- IRallyIn local-DB sync script: local -> match migrations
-- Generated from reference schema (all migrations applied)
-- Safe: all affected tables verified 0 rows; no FK constraints.
-- =========================================================

-- ########## 1. MISSING TABLES: ir_profile ##########
USE `ir_profile`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `court_change_requests` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `court_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联courts.id',
  `country` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '国家/地区',
  `province` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省/州',
  `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请修改后的球场名',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请修改后的地址',
  `contact_phone` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请修改后的联系电话',
  `wechat_mini_program_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请修改后的微信小程序名称',
  `photo_urls` json DEFAULT NULL COMMENT '申请修改后的球场照片URL数组',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '申请修改后的球场补充信息',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '申请修改后的纬度',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '申请修改后的经度',
  `map_source` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地图来源，如 appleMap',
  `surface_type` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请修改后的地面类型',
  `indoor_outdoor` enum('indoor','outdoor','both') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请修改后的室内外',
  `has_indoor` tinyint(1) DEFAULT NULL COMMENT '申请修改后是否有室内场地',
  `has_outdoor` tinyint(1) DEFAULT NULL COMMENT '申请修改后是否有室外场地',
  `total_court_count` int DEFAULT NULL COMMENT '申请修改后的球场总片数',
  `opening_time` time DEFAULT NULL COMMENT '申请修改后的每日营业开始时间',
  `closing_time` time DEFAULT NULL COMMENT '申请修改后的每日营业结束时间',
  `request_status` enum('pending','approved','voided','blacklisted') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '修改申请审核状态',
  `submitted_by` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提交修改申请的用户ID',
  `reviewed_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核人',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `rejected_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝/作废原因',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态: 0=正常, -1=删除',
  `pending_court_id` char(36) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when ((`request_status` = _utf8mb4'pending') and (`status` = 0)) then `court_id` else NULL end)) STORED,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_court_change_pending` (`pending_court_id`),
  KEY `idx_court_change_court` (`court_id`,`created_at` DESC),
  KEY `idx_court_change_status` (`request_status`,`created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网球场信息修改审核临时表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `court_likes` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `court_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联ir_profile.courts.id',
  `user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '点赞用户ir_auth.users.id',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态: 0=正常, -1=删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_court_likes_court_user` (`court_id`,`user_id`),
  KEY `idx_court_likes_court` (`court_id`),
  KEY `idx_court_likes_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网球场点赞记录';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `racket_catalog` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `brand` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品牌',
  `model` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '球拍型号',
  `model_zh` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '球拍中文名称',
  `unstrung_weight_gram` smallint DEFAULT NULL COMMENT '空拍质量(克)',
  `string_pattern` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '穿线方式',
  `balance_point_mm` smallint DEFAULT NULL COMMENT '平衡点(mm)',
  `length_inch` decimal(4,2) DEFAULT NULL COMMENT '长度(英寸)',
  `grip_size` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手柄型号: 1号/2号',
  `release_year` smallint DEFAULT NULL COMMENT '年份',
  `image_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片URL',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_racket_catalog_brand_model_year_grip` (`brand`,`model`,`release_year`,`grip_size`),
  KEY `idx_racket_catalog_brand` (`brand`),
  KEY `idx_racket_catalog_year` (`release_year`),
  KEY `idx_racket_catalog_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球拍基础数据';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `racket_player_usages` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `player_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '职业球员',
  `brand` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品牌',
  `model` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '球拍英文型号',
  `usage_year` smallint NOT NULL COMMENT '使用年份',
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_racket_player_year` (`player_name`,`brand`,`model`,`usage_year`),
  KEY `idx_racket_player_model` (`brand`,`model`),
  KEY `idx_racket_player_year` (`usage_year`),
  KEY `idx_racket_player_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业球员球拍使用基础数据';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_profile_permission_settings` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `gender_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别是否显示',
  `birthday_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '生日是否显示',
  `region_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '区域设置是否显示',
  `habit_courts_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '经常去的球场是否显示',
  `following_list_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '搭子列表是否显示',
  `follower_list_visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '粉丝列表是否显示',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0正常 1删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_profile_permission_user` (`user_id`),
  KEY `idx_user_profile_permission_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户主页权限设置';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


-- ########## 1b. MISSING TABLES: ir_activity ##########
USE `ir_activity`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_record_like_stats` (
  `session_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联play_sessions.id',
  `owner_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动记录作者ID',
  `total_like_count` int NOT NULL DEFAULT '0' COMMENT '点赞总次数',
  `last_liked_at` datetime DEFAULT NULL COMMENT '最近点赞时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`session_id`),
  KEY `idx_owner_total_likes` (`owner_id`,`total_like_count` DESC),
  KEY `idx_last_liked_at` (`last_liked_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='精彩记录点赞总表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_record_likes` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `session_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联play_sessions.id',
  `liker_user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '点赞用户ID',
  `like_count` int NOT NULL DEFAULT '1' COMMENT '该用户点赞次数',
  `first_liked_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次点赞时间',
  `last_liked_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近点赞时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态: 0=正常, -1=删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_record_liker` (`session_id`,`liker_user_id`),
  KEY `idx_session_recent` (`session_id`,`last_liked_at` DESC),
  KEY `idx_liker_recent` (`liker_user_id`,`last_liked_at` DESC),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='精彩记录点赞明细';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


-- ########## 2. ir_fitness DATABASE (entirely missing) ##########
CREATE DATABASE IF NOT EXISTS `ir_fitness` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ir_fitness` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `ir_fitness`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_body_metrics` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `healthkit_uuid` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'HealthKit sample UUID',
  `metric_type` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'heart_rate/hrv/spo2/body_mass等',
  `value` decimal(14,4) NOT NULL COMMENT '指标值',
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单位',
  `sampled_at` datetime NOT NULL COMMENT '采样时间',
  `source_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源',
  `raw_payload` json DEFAULT NULL COMMENT '原始字段快照',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=正常,-1=删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_metric_sample` (`user_id`,`metric_type`,`healthkit_uuid`),
  KEY `idx_user_metric_time` (`user_id`,`metric_type`,`sampled_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身身体与生理指标';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_daily_summaries` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `summary_date` date NOT NULL COMMENT '汇总日期',
  `step_count` int DEFAULT NULL COMMENT '每日总步数',
  `flights_climbed` int DEFAULT NULL COMMENT '爬楼层数',
  `walking_running_meters` decimal(12,2) DEFAULT NULL COMMENT '步行/跑步距离米',
  `cycling_meters` decimal(12,2) DEFAULT NULL COMMENT '骑行距离米',
  `swimming_meters` decimal(12,2) DEFAULT NULL COMMENT '游泳距离米',
  `basal_energy_kcal` decimal(10,2) DEFAULT NULL COMMENT '静态消耗',
  `active_energy_kcal` decimal(10,2) DEFAULT NULL COMMENT '动态活动热量',
  `stand_minutes` int DEFAULT NULL COMMENT '站立时长分钟',
  `exercise_minutes` int DEFAULT NULL COMMENT '锻炼分钟',
  `activity_ring_kcal` decimal(10,2) DEFAULT NULL COMMENT '活动圆环活动热量',
  `exercise_ring_minutes` int DEFAULT NULL COMMENT '活动圆环锻炼分钟',
  `stand_ring_hours` int DEFAULT NULL COMMENT '活动圆环站立小时',
  `source_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主要来源',
  `raw_payload` json DEFAULT NULL COMMENT 'HealthKit原始字段快照',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=正常,-1=删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_summary_date` (`user_id`,`summary_date`),
  KEY `idx_user_date` (`user_id`,`summary_date` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身每日活动汇总';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_sleep_sessions` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `healthkit_uuid` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '睡眠主记录UUID',
  `started_at` datetime NOT NULL COMMENT '开始时间',
  `ended_at` datetime NOT NULL COMMENT '结束时间',
  `total_sleep_minutes` int DEFAULT NULL COMMENT '总睡眠分钟',
  `deep_sleep_minutes` int DEFAULT NULL COMMENT '深睡分钟',
  `light_sleep_minutes` int DEFAULT NULL COMMENT '浅睡分钟',
  `awake_minutes` int DEFAULT NULL COMMENT '清醒分钟',
  `avg_night_heart_rate` decimal(6,2) DEFAULT NULL COMMENT '夜间平均心率',
  `turnover_count` int DEFAULT NULL COMMENT '翻身次数',
  `sleep_score` decimal(6,2) DEFAULT NULL COMMENT '睡眠评分',
  `source_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源',
  `raw_payload` json DEFAULT NULL COMMENT '原始字段快照',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=正常,-1=删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_sleep_uuid` (`user_id`,`healthkit_uuid`),
  KEY `idx_user_sleep_started` (`user_id`,`started_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身睡眠主记录';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_sleep_stages` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `sleep_session_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联fitness_sleep_sessions.id',
  `stage_type` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'deep/light/rem/awake等',
  `started_at` datetime NOT NULL COMMENT '开始时间',
  `ended_at` datetime NOT NULL COMMENT '结束时间',
  `duration_minutes` int DEFAULT NULL COMMENT '持续分钟',
  `raw_payload` json DEFAULT NULL COMMENT '原始字段快照',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sleep_session` (`sleep_session_id`,`started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身睡眠分段';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_strength_sets` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `workout_session_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联fitness_workout_sessions.id',
  `exercise_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '力量动作名称',
  `set_index` int NOT NULL COMMENT '第几组',
  `weight_kg` decimal(8,2) DEFAULT NULL COMMENT '重量kg',
  `reps` int DEFAULT NULL COMMENT '次数',
  `rm_value` decimal(8,2) DEFAULT NULL COMMENT 'RM值',
  `started_at` datetime DEFAULT NULL COMMENT '开始时间',
  `ended_at` datetime DEFAULT NULL COMMENT '结束时间',
  `raw_payload` json DEFAULT NULL COMMENT '原始字段快照',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_workout_exercise` (`workout_session_id`,`exercise_name`,`set_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身力量训练组';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_sync_sources` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `source_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源名称: Apple Watch/Keep/Strava等',
  `source_bundle_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源bundle id',
  `device_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备名称',
  `device_model` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备型号',
  `last_sync_at` datetime DEFAULT NULL COMMENT '最后同步时间',
  `permission_snapshot` json DEFAULT NULL COMMENT '授权快照',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=正常,-1=删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_source` (`user_id`,`source_name`,`source_bundle_id`),
  KEY `idx_user_sync` (`user_id`,`last_sync_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身数据同步来源';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_workout_samples` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `workout_session_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联fitness_workout_sessions.id',
  `sample_type` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'heart_rate/pace/elevation/power/cadence等',
  `sampled_at` datetime NOT NULL COMMENT '采样时间',
  `value` decimal(14,4) NOT NULL COMMENT '采样值',
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单位',
  `segment_index` int DEFAULT NULL COMMENT '分段序号',
  `raw_payload` json DEFAULT NULL COMMENT '原始字段快照',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_workout_sample_type_time` (`workout_session_id`,`sample_type`,`sampled_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身训练细粒度采样';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fitness_workout_sessions` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `user_id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `healthkit_uuid` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HealthKit HKWorkout UUID',
  `sport_type` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '运动类型',
  `started_at` datetime NOT NULL COMMENT '训练开始时间',
  `ended_at` datetime DEFAULT NULL COMMENT '训练结束时间',
  `duration_seconds` int DEFAULT NULL COMMENT '总时长秒',
  `active_energy_kcal` decimal(10,2) DEFAULT NULL COMMENT '活动卡路里',
  `basal_energy_kcal` decimal(10,2) DEFAULT NULL COMMENT '基础卡路里',
  `total_energy_kcal` decimal(10,2) DEFAULT NULL COMMENT '总卡路里',
  `distance_meters` decimal(12,2) DEFAULT NULL COMMENT '总距离米',
  `avg_heart_rate` decimal(6,2) DEFAULT NULL COMMENT '平均心率',
  `max_heart_rate` decimal(6,2) DEFAULT NULL COMMENT '最大心率',
  `heart_rate_zones` json DEFAULT NULL COMMENT '心率分区',
  `pace_seconds_per_km` decimal(8,2) DEFAULT NULL COMMENT '配速: 秒/公里',
  `speed_mps` decimal(8,3) DEFAULT NULL COMMENT '速度: 米/秒',
  `elevation_gain_meters` decimal(10,2) DEFAULT NULL COMMENT '爬升高度米',
  `cadence` decimal(8,2) DEFAULT NULL COMMENT '步频/踏频',
  `stroke_count` int DEFAULT NULL COMMENT '划水次数',
  `source_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源App/设备',
  `source_bundle_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源bundle id',
  `device_model` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备型号',
  `notes` text COLLATE utf8mb4_unicode_ci COMMENT '训练备注',
  `raw_payload` json DEFAULT NULL COMMENT 'HealthKit原始字段快照',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=正常,-1=删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_healthkit_workout` (`user_id`,`healthkit_uuid`),
  KEY `idx_user_started` (`user_id`,`started_at` DESC),
  KEY `idx_user_sport_started` (`user_id`,`sport_type`,`started_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身训练会话';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


-- ########## 3. courts (heavily diverged, 0 rows) -> recreate from migrations ##########
DROP TABLE IF EXISTS `ir_profile`.`courts`;
USE `ir_profile`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `courts` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID v4',
  `country` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '国家/地区',
  `province` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省/州',
  `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `area_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联areas.code',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '球场名',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `latitude` decimal(9,6) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(9,6) DEFAULT NULL COMMENT '经度',
  `map_source` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地图来源，如 appleMap',
  `surface_type` set('hard','clay','grass','sand_grass','carpet') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地面类型，可多选: hard=硬地, clay=红土, grass=草地, sand_grass=沙草, carpet=地毯',
  `indoor_outdoor` enum('indoor','outdoor','both') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '室内外',
  `has_indoor` tinyint(1) DEFAULT NULL COMMENT '是否有室内场地',
  `has_outdoor` tinyint(1) DEFAULT NULL COMMENT '是否有室外场地',
  `total_court_count` int DEFAULT NULL COMMENT '球场总片数',
  `indoor_court_count` int DEFAULT NULL COMMENT '室内球场片数',
  `outdoor_court_count` int DEFAULT NULL COMMENT '室外球场片数',
  `opening_time` time DEFAULT NULL COMMENT '每日营业开始时间',
  `closing_time` time DEFAULT NULL COMMENT '每日营业结束时间',
  `contact_phone` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `wechat_mini_program_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信小程序名称',
  `photo_urls` json DEFAULT NULL COMMENT '球场照片URL数组，建议3-5张',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '球场补充信息描述',
  `created_by` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提交该球场的用户ID',
  `amenities` json DEFAULT NULL COMMENT '设施如["parking","locker_room"]',
  `venue_status` enum('active','inactive','pending_review') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '场地运营状态',
  `approval_status` enum('pending','approved','rejected','voided','blacklisted') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'approved' COMMENT '平台审核状态',
  `operator_managed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否场馆主动运营',
  `reviewed_by` char(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核人ID',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审核时间',
  `rejected_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拒绝原因',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态: 0=正常, -1=删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_courts_name` (`name`),
  KEY `idx_area_code` (`area_code`),
  KEY `idx_lat_lng` (`latitude`,`longitude`),
  KEY `idx_country_city_name` (`country`,`city`,`name`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_approval_status` (`approval_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球场';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


-- ########## 4. club_events: add V1.3.0.20 extension columns (0 rows) ##########
ALTER TABLE `ir_club`.`club_events`
    ADD COLUMN `country` varchar(64) NULL COMMENT '国家/地区',
    ADD COLUMN `province` varchar(64) NULL COMMENT '省/州',
    ADD COLUMN `city` varchar(64) NULL COMMENT '城市',
    ADD COLUMN `district` varchar(64) NULL COMMENT '区县',
    ADD COLUMN `match_type` varchar(20) NULL COMMENT '约球类型',
    ADD COLUMN `needed_players` smallint NULL COMMENT '还缺人数',
    ADD COLUMN `min_level` decimal(3,1) NULL COMMENT '最低NTRP',
    ADD COLUMN `max_level` decimal(3,1) NULL COMMENT '最高NTRP',
    ADD COLUMN `price_mode` varchar(20) NULL COMMENT '费用模式',
    ADD COLUMN `price_per_person` int NULL COMMENT '每人费用',
    ADD COLUMN `gender_requirement` varchar(20) NULL COMMENT '性别要求',
    ADD COLUMN `note` varchar(300) NULL COMMENT '约球备注',
    MODIFY COLUMN `club_id` CHAR(36) NULL COMMENT '关联俱乐部ID';

-- ########## 5. play_sessions: add healthkit_uuid (V1.3.0.24, 0 rows) ##########
ALTER TABLE `ir_activity`.`play_sessions`
    ADD COLUMN `healthkit_uuid` varchar(80) NULL COMMENT '来源 HealthKit HKWorkout UUID';
