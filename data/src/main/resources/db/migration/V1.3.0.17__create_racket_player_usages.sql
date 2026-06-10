CREATE TABLE IF NOT EXISTS `ir_profile`.`racket_player_usages` (
    `id` CHAR(36) NOT NULL COMMENT 'UUID v4',
    `player_name` VARCHAR(100) NOT NULL COMMENT '职业球员',
    `brand` VARCHAR(60) NOT NULL COMMENT '品牌',
    `model` VARCHAR(120) NOT NULL COMMENT '球拍英文型号',
    `usage_year` SMALLINT NOT NULL COMMENT '使用年份',
    `notes` VARCHAR(255) NULL COMMENT '备注',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_racket_player_year` (`player_name`, `brand`, `model`, `usage_year`),
    KEY `idx_racket_player_model` (`brand`, `model`),
    KEY `idx_racket_player_year` (`usage_year`),
    KEY `idx_racket_player_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业球员球拍使用基础数据';

INSERT IGNORE INTO `ir_profile`.`racket_player_usages`
(`id`, `player_name`, `brand`, `model`, `usage_year`, `notes`, `status`)
VALUES
('c0000001-0000-4000-8000-000000000001', 'Pete Sampras', 'Wilson', 'Pro Staff Original 6.0 85', 1984, '经典 Pro Staff 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000002', 'Stefan Edberg', 'Wilson', 'Pro Staff Original 6.0 85', 1985, '经典 Pro Staff 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000003', 'John McEnroe', 'Dunlop', 'Max 200G', 1988, 'Dunlop 经典球拍代表使用者', 0),
('c0000001-0000-4000-8000-000000000004', 'Steffi Graf', 'Dunlop', 'Max 200G', 1988, 'Dunlop 经典球拍代表使用者', 0),
('c0000001-0000-4000-8000-000000000005', 'Michael Chang', 'Prince', 'Graphite Original Oversize', 1989, 'Prince Graphite 经典系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000006', 'Andre Agassi', 'HEAD', 'Liquidmetal Radical MP', 2003, 'Radical 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000007', 'Andy Roddick', 'Babolat', 'Pure Drive Team', 2003, 'Pure Drive 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000008', 'Rafael Nadal', 'Babolat', 'AeroPro Drive', 2005, 'Aero 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000009', 'Roger Federer', 'Wilson', 'Pro Staff RF97 Autograph', 2014, 'RF97 代表使用者', 0),
('c0000001-0000-4000-8000-000000000010', 'Stan Wawrinka', 'Yonex', 'VCORE 98', 2018, 'Yonex VCORE 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000011', 'Daniil Medvedev', 'Tecnifibre', 'TFight 305 RS', 2021, 'Tecnifibre TFight 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000012', 'Nick Kyrgios', 'Yonex', 'EZONE 98', 2022, 'Yonex EZONE 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000013', 'Naomi Osaka', 'Yonex', 'EZONE 98', 2022, 'Yonex EZONE 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000014', 'Carlos Alcaraz', 'Babolat', 'Pure Aero', 2023, 'Pure Aero 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000015', 'Novak Djokovic', 'HEAD', 'Speed MP', 2024, 'HEAD Speed 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000016', 'Jannik Sinner', 'HEAD', 'Speed MP', 2024, 'HEAD Speed 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000017', 'Aryna Sabalenka', 'Wilson', 'Blade 98 v9', 2024, 'Wilson Blade 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000018', 'Alex de Minaur', 'Wilson', 'Blade 98 v9', 2024, 'Wilson Blade 系列代表使用者', 0),
('c0000001-0000-4000-8000-000000000019', 'Andrey Rublev', 'HEAD', 'Gravity MP', 2024, 'Gravity 系列代表使用者，球拍基础库后续可补型号', 0),
('c0000001-0000-4000-8000-000000000020', 'Taylor Fritz', 'HEAD', 'Radical MP', 2023, 'Radical 系列代表使用者', 0);
