CREATE TABLE IF NOT EXISTS `ir_profile`.`racket_catalog` (
    `id` CHAR(36) NOT NULL COMMENT 'UUID v4',
    `brand` VARCHAR(60) NOT NULL COMMENT '品牌',
    `model` VARCHAR(120) NOT NULL COMMENT '球拍型号',
    `unstrung_weight_gram` SMALLINT NULL COMMENT '空拍质量(克)',
    `string_pattern` VARCHAR(20) NULL COMMENT '穿线方式',
    `balance_point_mm` SMALLINT NULL COMMENT '平衡点(mm)',
    `length_inch` DECIMAL(4,2) NULL COMMENT '长度(英寸)',
    `grip_size` VARCHAR(10) NULL COMMENT '手柄型号: 1号/2号',
    `release_year` SMALLINT NULL COMMENT '年份',
    `image_url` VARCHAR(512) NULL COMMENT '图片URL',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_racket_catalog_brand_model_year_grip` (`brand`, `model`, `release_year`, `grip_size`),
    KEY `idx_racket_catalog_brand` (`brand`),
    KEY `idx_racket_catalog_year` (`release_year`),
    KEY `idx_racket_catalog_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球拍基础数据';

INSERT IGNORE INTO `ir_profile`.`racket_catalog`
(`id`, `brand`, `model`, `unstrung_weight_gram`, `string_pattern`, `balance_point_mm`, `length_inch`, `grip_size`, `release_year`, `image_url`, `status`)
SELECT UUID(),
       seed.`brand`,
       seed.`model`,
       seed.`unstrung_weight_gram`,
       seed.`string_pattern`,
       seed.`balance_point_mm`,
       seed.`length_inch`,
       grips.`grip_size`,
       seed.`release_year`,
       NULL,
       0
FROM (
    SELECT 'Wilson' AS `brand`, 'Pro Staff Original 6.0 85' AS `model`, 340 AS `unstrung_weight_gram`, '16x18' AS `string_pattern`, 305 AS `balance_point_mm`, 27.00 AS `length_inch`, 1984 AS `release_year`
    UNION ALL SELECT 'Prince', 'Graphite Original Oversize', 330, '16x19', 315, 27.00, 1987
    UNION ALL SELECT 'Wilson', 'Profile 2.7 Oversize', 335, '16x19', 335, 27.00, 1987
    UNION ALL SELECT 'Dunlop', 'Max 200G', 354, '18x20', 320, 27.00, 1988
    UNION ALL SELECT 'Yonex', 'R-22', 340, '16x19', 315, 27.00, 1989
    UNION ALL SELECT 'Wilson', 'Pro Staff Classic 6.1 95', 332, '16x18', 310, 27.00, 1991
    UNION ALL SELECT 'HEAD', 'Prestige Classic 600', 330, '18x20', 310, 27.00, 1993
    UNION ALL SELECT 'Yonex', 'RD-7', 330, '16x19', 315, 27.00, 1995
    UNION ALL SELECT 'Dunlop', 'Muscle Weave 200G', 320, '18x20', 315, 27.00, 1997
    UNION ALL SELECT 'Wilson', 'Hyper Pro Staff 6.1 95', 332, '16x18', 310, 27.00, 2001
    UNION ALL SELECT 'HEAD', 'Liquidmetal Radical MP', 295, '18x20', 325, 27.00, 2003
    UNION ALL SELECT 'Babolat', 'Pure Drive Team', 300, '16x19', 320, 27.00, 2003
    UNION ALL SELECT 'Babolat', 'AeroPro Drive', 300, '16x19', 320, 27.00, 2004
    UNION ALL SELECT 'Tecnifibre', 'T-Fight 325', 325, '18x20', 310, 27.00, 2005
    UNION ALL SELECT 'Yonex', 'RDS 001 Midplus', 315, '16x19', 310, 27.00, 2006
    UNION ALL SELECT 'Wilson', 'K Factor KSix-One 95', 332, '16x18', 310, 27.00, 2007
    UNION ALL SELECT 'Wilson', 'K Factor KPro Staff 88', 349, '16x19', 315, 27.00, 2008
    UNION ALL SELECT 'Dunlop', 'Aerogel 4D 200', 320, '18x20', 315, 27.00, 2009
    UNION ALL SELECT 'HEAD', 'YouTek Speed MP', 315, '18x20', 315, 27.00, 2009
    UNION ALL SELECT 'Babolat', 'Pure Storm GT', 295, '16x20', 325, 27.00, 2010
    UNION ALL SELECT 'Wilson', 'BLX Blade 98', 304, '18x20', 325, 27.00, 2011
    UNION ALL SELECT 'Yonex', 'VCORE 98D', 305, '16x20', 315, 27.00, 2011
    UNION ALL SELECT 'HEAD', 'Graphene Speed MP', 300, '16x19', 320, 27.00, 2013
    UNION ALL SELECT 'Babolat', 'Pure Drive', 300, '16x19', 320, 27.00, 2015
    UNION ALL SELECT 'Wilson', 'Pro Staff RF97 Autograph', 340, '16x19', 305, 27.00, 2014
    UNION ALL SELECT 'Yonex', 'EZONE DR 98', 310, '16x19', 310, 27.00, 2015
    UNION ALL SELECT 'Dunlop', 'Precision 98', 305, '16x19', 320, 27.00, 2016
    UNION ALL SELECT 'Tecnifibre', 'T-Fight 315 Ltd', 315, '18x20', 310, 27.00, 2016
    UNION ALL SELECT 'Babolat', 'Pure Aero', 300, '16x19', 320, 27.00, 2016
    UNION ALL SELECT 'Wilson', 'Blade 98 Countervail', 304, '16x19', 320, 27.00, 2017
    UNION ALL SELECT 'HEAD', 'Graphene 360 Radical MP', 295, '16x19', 320, 27.00, 2018
    UNION ALL SELECT 'Yonex', 'VCORE 98', 305, '16x19', 315, 27.00, 2018
    UNION ALL SELECT 'Dunlop', 'CX 200 Tour 18x20', 315, '18x20', 315, 27.00, 2019
    UNION ALL SELECT 'Wilson', 'Clash 100', 295, '16x19', 320, 27.00, 2019
    UNION ALL SELECT 'ProKennex', 'Ki Q+ Tour Pro 315', 315, '16x19', 310, 27.00, 2019
    UNION ALL SELECT 'Babolat', 'Pure Strike 16x19', 305, '16x19', 320, 27.00, 2020
    UNION ALL SELECT 'HEAD', 'Graphene 360+ Prestige MP', 320, '18x20', 310, 27.00, 2020
    UNION ALL SELECT 'Yonex', 'EZONE 98', 305, '16x19', 315, 27.00, 2020
    UNION ALL SELECT 'Dunlop', 'SX 300', 300, '16x19', 320, 27.00, 2020
    UNION ALL SELECT 'Wilson', 'Blade 98 v8', 305, '16x19', 320, 27.00, 2021
    UNION ALL SELECT 'Tecnifibre', 'TFight 305 RS', 305, '18x19', 325, 27.00, 2021
    UNION ALL SELECT 'HEAD', 'Boom MP', 295, '16x19', 315, 27.00, 2022
    UNION ALL SELECT 'Dunlop', 'FX 500', 300, '16x19', 320, 27.00, 2022
    UNION ALL SELECT 'Yonex', 'EZONE 100', 300, '16x19', 320, 27.00, 2022
    UNION ALL SELECT 'Babolat', 'Pure Aero', 300, '16x19', 320, 27.00, 2023
    UNION ALL SELECT 'HEAD', 'Radical MP', 300, '16x19', 320, 27.00, 2023
    UNION ALL SELECT 'Wilson', 'Pro Staff 97 v14', 315, '16x19', 310, 27.00, 2023
    UNION ALL SELECT 'Yonex', 'VCORE 98', 305, '16x19', 315, 27.00, 2023
    UNION ALL SELECT 'Dunlop', 'CX 200', 305, '16x19', 320, 27.00, 2024
    UNION ALL SELECT 'HEAD', 'Speed MP', 300, '16x19', 320, 27.00, 2024
    UNION ALL SELECT 'Wilson', 'Blade 98 v9', 305, '16x19', 320, 27.00, 2024
    UNION ALL SELECT 'Wilson', 'Clash 100 v3', 295, '16x19', 320, 27.00, 2025
    UNION ALL SELECT 'Babolat', 'Pure Drive', 300, '16x19', 320, 27.00, 2025
    UNION ALL SELECT 'Yonex', 'EZONE 98', 305, '16x19', 315, 27.00, 2025
    UNION ALL SELECT 'Li-Ning', 'Turbo Charging N9 II Tennis', 300, '16x19', 320, 27.00, 2021
    UNION ALL SELECT 'Li-Ning', 'Razor RZ 98', 305, '16x19', 315, 27.00, 2022
    UNION ALL SELECT 'Li-Ning', 'Tectonic 7 Tennis', 300, '16x19', 320, 27.00, 2023
    UNION ALL SELECT 'Li-Ning', 'Halbertec 800 Tennis', 300, '16x19', 320, 27.00, 2024
    UNION ALL SELECT 'Prince', 'Phantom 100X 305', 305, '16x18', 315, 27.00, 2024
    UNION ALL SELECT 'Volkl', 'V-Cell 10 300g', 300, '16x19', 320, 27.00, 2021
    UNION ALL SELECT 'Solinco', 'Whiteout 305', 305, '16x19', 320, 27.00, 2022
    UNION ALL SELECT 'Artengo', 'TR960 Control Tour', 305, '16x19', 315, 27.00, 2023
) seed
JOIN (
    SELECT '1号' AS `grip_size`
    UNION ALL SELECT '2号'
) grips;
