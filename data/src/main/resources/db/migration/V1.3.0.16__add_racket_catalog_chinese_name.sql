SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'ir_profile'
      AND TABLE_NAME = 'racket_catalog'
      AND COLUMN_NAME = 'model_zh'
);

SET @ddl = IF(
    @column_exists = 0,
    'ALTER TABLE `ir_profile`.`racket_catalog` ADD COLUMN `model_zh` VARCHAR(120) NULL COMMENT ''球拍中文名称'' AFTER `model`',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `ir_profile`.`racket_catalog`
SET `model_zh` = CASE `model`
    WHEN 'Pro Staff Original 6.0 85' THEN '职业员工经典 6.0 85'
    WHEN 'Graphite Original Oversize' THEN '石墨经典大拍面'
    WHEN 'Profile 2.7 Oversize' THEN 'Profile 2.7 大拍面'
    WHEN 'Max 200G' THEN '马克斯 200G'
    WHEN 'R-22' THEN 'R-22'
    WHEN 'Pro Staff Classic 6.1 95' THEN '职业员工经典 6.1 95'
    WHEN 'Prestige Classic 600' THEN '经典威望 600'
    WHEN 'RD-7' THEN 'RD-7'
    WHEN 'Muscle Weave 200G' THEN '肌肉编织 200G'
    WHEN 'Hyper Pro Staff 6.1 95' THEN '超能职业员工 6.1 95'
    WHEN 'Liquidmetal Radical MP' THEN '液态金属激进 MP'
    WHEN 'Pure Drive Team' THEN '纯驱团队版'
    WHEN 'AeroPro Drive' THEN '空气专业驱动'
    WHEN 'T-Fight 325' THEN 'T-Fight 325'
    WHEN 'RDS 001 Midplus' THEN 'RDS 001 中拍面'
    WHEN 'K Factor KSix-One 95' THEN 'K 因子 KSix-One 95'
    WHEN 'K Factor KPro Staff 88' THEN 'K 因子 KPro Staff 88'
    WHEN 'Aerogel 4D 200' THEN '气凝胶 4D 200'
    WHEN 'YouTek Speed MP' THEN 'YouTek 速度 MP'
    WHEN 'Pure Storm GT' THEN '纯风暴 GT'
    WHEN 'BLX Blade 98' THEN 'BLX 刀锋 98'
    WHEN 'VCORE 98D' THEN 'VCORE 98D'
    WHEN 'Graphene Speed MP' THEN '石墨烯速度 MP'
    WHEN 'Pure Drive' THEN '纯驱'
    WHEN 'Pro Staff RF97 Autograph' THEN '职业员工 RF97 亲签版'
    WHEN 'EZONE DR 98' THEN 'EZONE DR 98'
    WHEN 'Precision 98' THEN '精准 98'
    WHEN 'T-Fight 315 Ltd' THEN 'T-Fight 315 限量版'
    WHEN 'Pure Aero' THEN '纯 Aero'
    WHEN 'Blade 98 Countervail' THEN '刀锋 98 Countervail'
    WHEN 'Graphene 360 Radical MP' THEN '石墨烯 360 激进 MP'
    WHEN 'VCORE 98' THEN 'VCORE 98'
    WHEN 'CX 200 Tour 18x20' THEN 'CX 200 巡回 18x20'
    WHEN 'Clash 100' THEN '克拉什 100'
    WHEN 'Ki Q+ Tour Pro 315' THEN 'Ki Q+ 巡回专业 315'
    WHEN 'Pure Strike 16x19' THEN '纯击 16x19'
    WHEN 'Graphene 360+ Prestige MP' THEN '石墨烯 360+ 威望 MP'
    WHEN 'EZONE 98' THEN 'EZONE 98'
    WHEN 'SX 300' THEN 'SX 300'
    WHEN 'Blade 98 v8' THEN '刀锋 98 v8'
    WHEN 'TFight 305 RS' THEN 'TFight 305 RS'
    WHEN 'Boom MP' THEN 'Boom MP'
    WHEN 'FX 500' THEN 'FX 500'
    WHEN 'EZONE 100' THEN 'EZONE 100'
    WHEN 'Radical MP' THEN '激进 MP'
    WHEN 'Pro Staff 97 v14' THEN '职业员工 97 v14'
    WHEN 'CX 200' THEN 'CX 200'
    WHEN 'Speed MP' THEN '速度 MP'
    WHEN 'Blade 98 v9' THEN '刀锋 98 v9'
    WHEN 'Clash 100 v3' THEN '克拉什 100 v3'
    WHEN 'Turbo Charging N9 II Tennis' THEN '风动 N9 II 网球拍'
    WHEN 'Razor RZ 98' THEN '锋影 RZ 98'
    WHEN 'Tectonic 7 Tennis' THEN '能量 7 网球拍'
    WHEN 'Halbertec 800 Tennis' THEN '战戟 800 网球拍'
    WHEN 'Phantom 100X 305' THEN '幻影 100X 305'
    WHEN 'V-Cell 10 300g' THEN 'V-Cell 10 300g'
    WHEN 'Whiteout 305' THEN 'Whiteout 305'
    WHEN 'TR960 Control Tour' THEN 'TR960 控制巡回'
    ELSE `model`
END
WHERE (`model_zh` IS NULL OR `model_zh` = '')
  AND `status` = 0;
