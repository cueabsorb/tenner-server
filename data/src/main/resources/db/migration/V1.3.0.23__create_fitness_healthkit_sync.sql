-- ============================================================
-- IRallyIn: fitness HealthKit sync schema
-- HealthKit is the single ingestion source on iOS; source_name/source_bundle_id
-- preserve the original app/device that wrote data into HealthKit.
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ir_fitness` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_fitness`;

CREATE TABLE IF NOT EXISTS `fitness_sync_sources` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '用户ID',
    `source_name`           VARCHAR(100)    NOT NULL COMMENT '来源名称: Apple Watch/Keep/Strava等',
    `source_bundle_id`      VARCHAR(200)    NULL     COMMENT '来源bundle id',
    `device_name`           VARCHAR(100)    NULL     COMMENT '设备名称',
    `device_model`          VARCHAR(100)    NULL     COMMENT '设备型号',
    `last_sync_at`          DATETIME        NULL     COMMENT '最后同步时间',
    `permission_snapshot`   JSON            NULL     COMMENT '授权快照',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '0=正常,-1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_source` (`user_id`, `source_name`, `source_bundle_id`),
    KEY `idx_user_sync` (`user_id`, `last_sync_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身数据同步来源';

CREATE TABLE IF NOT EXISTS `fitness_workout_sessions` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '用户ID',
    `healthkit_uuid`        VARCHAR(80)     NOT NULL COMMENT 'HealthKit HKWorkout UUID',
    `sport_type`            VARCHAR(60)     NOT NULL COMMENT '运动类型',
    `started_at`            DATETIME        NOT NULL COMMENT '训练开始时间',
    `ended_at`              DATETIME        NULL     COMMENT '训练结束时间',
    `duration_seconds`      INT             NULL     COMMENT '总时长秒',
    `active_energy_kcal`    DECIMAL(10,2)   NULL     COMMENT '活动卡路里',
    `basal_energy_kcal`     DECIMAL(10,2)   NULL     COMMENT '基础卡路里',
    `total_energy_kcal`     DECIMAL(10,2)   NULL     COMMENT '总卡路里',
    `distance_meters`       DECIMAL(12,2)   NULL     COMMENT '总距离米',
    `avg_heart_rate`        DECIMAL(6,2)    NULL     COMMENT '平均心率',
    `max_heart_rate`        DECIMAL(6,2)    NULL     COMMENT '最大心率',
    `heart_rate_zones`      JSON            NULL     COMMENT '心率分区',
    `pace_seconds_per_km`   DECIMAL(8,2)    NULL     COMMENT '配速: 秒/公里',
    `speed_mps`             DECIMAL(8,3)    NULL     COMMENT '速度: 米/秒',
    `elevation_gain_meters` DECIMAL(10,2)   NULL     COMMENT '爬升高度米',
    `cadence`               DECIMAL(8,2)    NULL     COMMENT '步频/踏频',
    `stroke_count`          INT             NULL     COMMENT '划水次数',
    `source_name`           VARCHAR(100)    NULL     COMMENT '来源App/设备',
    `source_bundle_id`      VARCHAR(200)    NULL     COMMENT '来源bundle id',
    `device_model`          VARCHAR(100)    NULL     COMMENT '设备型号',
    `notes`                 TEXT            NULL     COMMENT '训练备注',
    `raw_payload`           JSON            NULL     COMMENT 'HealthKit原始字段快照',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '0=正常,-1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_healthkit_workout` (`user_id`, `healthkit_uuid`),
    KEY `idx_user_started` (`user_id`, `started_at` DESC),
    KEY `idx_user_sport_started` (`user_id`, `sport_type`, `started_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身训练会话';

CREATE TABLE IF NOT EXISTS `fitness_daily_summaries` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '用户ID',
    `summary_date`          DATE            NOT NULL COMMENT '汇总日期',
    `step_count`            INT             NULL     COMMENT '每日总步数',
    `flights_climbed`       INT             NULL     COMMENT '爬楼层数',
    `walking_running_meters` DECIMAL(12,2)  NULL     COMMENT '步行/跑步距离米',
    `cycling_meters`        DECIMAL(12,2)   NULL     COMMENT '骑行距离米',
    `swimming_meters`       DECIMAL(12,2)   NULL     COMMENT '游泳距离米',
    `basal_energy_kcal`     DECIMAL(10,2)   NULL     COMMENT '静态消耗',
    `active_energy_kcal`    DECIMAL(10,2)   NULL     COMMENT '动态活动热量',
    `stand_minutes`         INT             NULL     COMMENT '站立时长分钟',
    `exercise_minutes`      INT             NULL     COMMENT '锻炼分钟',
    `activity_ring_kcal`    DECIMAL(10,2)   NULL     COMMENT '活动圆环活动热量',
    `exercise_ring_minutes` INT             NULL     COMMENT '活动圆环锻炼分钟',
    `stand_ring_hours`      INT             NULL     COMMENT '活动圆环站立小时',
    `source_name`           VARCHAR(100)    NULL     COMMENT '主要来源',
    `raw_payload`           JSON            NULL     COMMENT 'HealthKit原始字段快照',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '0=正常,-1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_summary_date` (`user_id`, `summary_date`),
    KEY `idx_user_date` (`user_id`, `summary_date` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身每日活动汇总';

CREATE TABLE IF NOT EXISTS `fitness_body_metrics` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '用户ID',
    `healthkit_uuid`        VARCHAR(80)     NULL     COMMENT 'HealthKit sample UUID',
    `metric_type`           VARCHAR(60)     NOT NULL COMMENT 'heart_rate/hrv/spo2/body_mass等',
    `value`                 DECIMAL(14,4)   NOT NULL COMMENT '指标值',
    `unit`                  VARCHAR(30)     NOT NULL COMMENT '单位',
    `sampled_at`            DATETIME        NOT NULL COMMENT '采样时间',
    `source_name`           VARCHAR(100)    NULL     COMMENT '来源',
    `raw_payload`           JSON            NULL     COMMENT '原始字段快照',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '0=正常,-1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_metric_sample` (`user_id`, `metric_type`, `healthkit_uuid`),
    KEY `idx_user_metric_time` (`user_id`, `metric_type`, `sampled_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身身体与生理指标';

CREATE TABLE IF NOT EXISTS `fitness_sleep_sessions` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `user_id`               CHAR(36)        NOT NULL COMMENT '用户ID',
    `healthkit_uuid`        VARCHAR(80)     NULL     COMMENT '睡眠主记录UUID',
    `started_at`            DATETIME        NOT NULL COMMENT '开始时间',
    `ended_at`              DATETIME        NOT NULL COMMENT '结束时间',
    `total_sleep_minutes`   INT             NULL     COMMENT '总睡眠分钟',
    `deep_sleep_minutes`    INT             NULL     COMMENT '深睡分钟',
    `light_sleep_minutes`   INT             NULL     COMMENT '浅睡分钟',
    `awake_minutes`         INT             NULL     COMMENT '清醒分钟',
    `avg_night_heart_rate`  DECIMAL(6,2)    NULL     COMMENT '夜间平均心率',
    `turnover_count`        INT             NULL     COMMENT '翻身次数',
    `sleep_score`           DECIMAL(6,2)    NULL     COMMENT '睡眠评分',
    `source_name`           VARCHAR(100)    NULL     COMMENT '来源',
    `raw_payload`           JSON            NULL     COMMENT '原始字段快照',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '0=正常,-1=删除',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sleep_uuid` (`user_id`, `healthkit_uuid`),
    KEY `idx_user_sleep_started` (`user_id`, `started_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身睡眠主记录';

CREATE TABLE IF NOT EXISTS `fitness_sleep_stages` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `sleep_session_id`      CHAR(36)        NOT NULL COMMENT '关联fitness_sleep_sessions.id',
    `stage_type`            VARCHAR(40)     NOT NULL COMMENT 'deep/light/rem/awake等',
    `started_at`            DATETIME        NOT NULL COMMENT '开始时间',
    `ended_at`              DATETIME        NOT NULL COMMENT '结束时间',
    `duration_minutes`      INT             NULL     COMMENT '持续分钟',
    `raw_payload`           JSON            NULL     COMMENT '原始字段快照',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sleep_session` (`sleep_session_id`, `started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身睡眠分段';

CREATE TABLE IF NOT EXISTS `fitness_workout_samples` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `workout_session_id`    CHAR(36)        NOT NULL COMMENT '关联fitness_workout_sessions.id',
    `sample_type`           VARCHAR(60)     NOT NULL COMMENT 'heart_rate/pace/elevation/power/cadence等',
    `sampled_at`            DATETIME        NOT NULL COMMENT '采样时间',
    `value`                 DECIMAL(14,4)   NOT NULL COMMENT '采样值',
    `unit`                  VARCHAR(30)     NOT NULL COMMENT '单位',
    `segment_index`         INT             NULL     COMMENT '分段序号',
    `raw_payload`           JSON            NULL     COMMENT '原始字段快照',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_workout_sample_type_time` (`workout_session_id`, `sample_type`, `sampled_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身训练细粒度采样';

CREATE TABLE IF NOT EXISTS `fitness_strength_sets` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `workout_session_id`    CHAR(36)        NOT NULL COMMENT '关联fitness_workout_sessions.id',
    `exercise_name`         VARCHAR(100)    NOT NULL COMMENT '力量动作名称',
    `set_index`             INT             NOT NULL COMMENT '第几组',
    `weight_kg`             DECIMAL(8,2)    NULL     COMMENT '重量kg',
    `reps`                  INT             NULL     COMMENT '次数',
    `rm_value`              DECIMAL(8,2)    NULL     COMMENT 'RM值',
    `started_at`            DATETIME        NULL     COMMENT '开始时间',
    `ended_at`              DATETIME        NULL     COMMENT '结束时间',
    `raw_payload`           JSON            NULL     COMMENT '原始字段快照',
    `created_at`            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_workout_exercise` (`workout_session_id`, `exercise_name`, `set_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身力量训练组';
