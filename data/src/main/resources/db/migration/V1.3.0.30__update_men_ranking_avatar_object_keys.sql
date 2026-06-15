UPDATE `ir_profile`.`world_ranking_players`
SET `avatar_url` = CASE `name`
    WHEN '扬尼克·辛纳' THEN 'image/atp/man/JannikSinner_head_2026.png'
    WHEN '卡洛斯·阿尔卡拉斯' THEN 'image/atp/man/CarlosAlcaraz_head_2024.png'
    WHEN '亚历山大·兹维列夫' THEN 'image/atp/man/AlexanderZverev_2026.png'
    WHEN '泰勒·弗里茨' THEN 'image/atp/man/TaylorFritz_head_2026.png'
    WHEN '诺瓦克·德约科维奇' THEN 'image/atp/man/NovakDjokovic_head_2026.png'
    WHEN '亚历克斯·德米纳尔' THEN 'image/atp/man/Alexde-minaur_head_2026_final.png'
    WHEN '丹尼尔·梅德韦杰夫' THEN 'image/atp/man/DaniilMedvedev_head_2026.png'
    WHEN '本·谢尔顿' THEN 'image/atp/man/BenShelton_head_2025.png'
    WHEN '安德烈·鲁布列夫' THEN 'image/atp/man/rublev_head_2026.png'
    WHEN '费利克斯·奥热-阿利亚西姆' THEN 'image/atp/man/FelixAuger-Aliassime_head_2026.png'
    WHEN '吉里·莱赫奇卡' THEN 'image/atp/man/Jiri Lehecka_head_2026.png'
    WHEN '亚历山大·布勃利克' THEN 'image/atp/man/AlexanderBublik_head_2026.png'
    WHEN '弗拉维奥·科博利' THEN 'image/atp/man/FlavioCobolli_head_2026.png'
    ELSE `avatar_url`
END
WHERE `gender` = 'men'
  AND `status` = 0
  AND `name` IN (
      '扬尼克·辛纳',
      '卡洛斯·阿尔卡拉斯',
      '亚历山大·兹维列夫',
      '泰勒·弗里茨',
      '诺瓦克·德约科维奇',
      '亚历克斯·德米纳尔',
      '丹尼尔·梅德韦杰夫',
      '本·谢尔顿',
      '安德烈·鲁布列夫',
      '费利克斯·奥热-阿利亚西姆',
      '吉里·莱赫奇卡',
      '亚历山大·布勃利克',
      '弗拉维奥·科博利'
  );
