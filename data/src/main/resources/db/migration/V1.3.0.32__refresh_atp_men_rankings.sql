SET @new_snapshot_id = '20260615-0000-4000-8000-atpmen0001';
SET @previous_snapshot_id = (
    SELECT `id`
    FROM `ir_profile`.`ranking_snapshots`
    WHERE `status` = 0
      AND `id` <> @new_snapshot_id
    ORDER BY `updated_at` DESC, `created_at` DESC
    LIMIT 1
);

INSERT IGNORE INTO `ir_profile`.`ranking_snapshots` (`id`, `updated_at`, `next_refresh_at`, `status`)
VALUES (@new_snapshot_id, '2026-06-15', '2026-06-22', 0);

UPDATE `ir_profile`.`world_ranking_players`
SET `status` = -1
WHERE `gender` = 'men'
  AND `source` = 'ATP'
  AND `status` = 0;

INSERT IGNORE INTO `ir_profile`.`world_ranking_players`
(`id`, `snapshot_id`, `gender`, `rank_no`, `name`, `country`, `points`, `ntrp`, `source`, `avatar_url`, `status`)
VALUES
(UUID(), @new_snapshot_id, 'men', 1, 'Jannik Sinner', '', 13500, 7.0, 'ATP', 'image/atp/man/JannikSinner_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 2, 'Carlos Alcaraz', '', 9960, 7.0, 'ATP', 'image/atp/man/CarlosAlcaraz_head_2024.png', 0),
(UUID(), @new_snapshot_id, 'men', 3, 'Alexander Zverev', '', 7190, 7.0, 'ATP', 'image/atp/man/AlexanderZverev_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 4, 'Felix Auger-Aliassime', '', 4390, 7.0, 'ATP', 'image/atp/man/FelixAuger-Aliassime_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 5, 'Ben Shelton', '', 4070, 7.0, 'ATP', 'image/atp/man/BenShelton_head_2025.png', 0),
(UUID(), @new_snapshot_id, 'men', 6, 'Alex de Minaur', '', 4060, 7.0, 'ATP', 'image/atp/man/Alexde-minaur_head_2026_final.png', 0),
(UUID(), @new_snapshot_id, 'men', 7, 'Daniil Medvedev', '', 3810, 7.0, 'ATP', 'image/atp/man/DaniilMedvedev_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 8, 'Novak Djokovic', '', 3760, 7.0, 'ATP', 'image/atp/man/NovakDjokovic_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 9, 'Taylor Fritz', '', 3635, 7.0, 'ATP', 'image/atp/man/TaylorFritz_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 10, 'Flavio Cobolli', '', 3540, 7.0, 'ATP', 'image/atp/man/FlavioCobolli_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 11, 'Alexander Bublik', '', 3020, 7.0, 'ATP', 'image/atp/man/AlexanderBublik_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 12, 'Jiri Lehecka', '', 2640, 7.0, 'ATP', 'image/atp/man/Jiri Lehecka_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 13, 'Andrey Rublev', '', 2460, 7.0, 'ATP', 'image/atp/man/rublev_head_2026.png', 0),
(UUID(), @new_snapshot_id, 'men', 14, 'Casper Ruud', '', 2425, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 15, 'Lorenzo Musetti', '', 2315, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 16, 'Jakub Mensik', '', 2300, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 17, 'Luciano Darderi', '', 2300, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 18, 'Karen Khachanov', '', 2280, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 19, 'Learner Tien', '', 2270, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 20, 'Valentin Vacherot', '', 2138, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 21, 'Arthur Fils', '', 1940, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 22, 'Alejandro Davidovich Fokina', '', 1860, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 23, 'Rafael Jodar', '', 1849, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 24, 'Arthur Rinderknech', '', 1776, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 25, 'Joao Fonseca', '', 1735, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 26, 'Frances Tiafoe', '', 1730, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 27, 'Francisco Cerundolo', '', 1660, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 28, 'Tommy Paul', '', 1645, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 29, 'Cameron Norrie', '', 1595, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 30, 'Tomas Martin Etcheverry', '', 1510, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 31, 'Alejandro Tabilo', '', 1428, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 32, 'Brandon Nakashima', '', 1385, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 33, 'Ugo Humbert', '', 1360, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 34, 'Matteo Arnaldi', '', 1336, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 35, 'Ignacio Buse', '', 1316, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 36, 'Corentin Moutet', '', 1283, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 37, 'Alexander Blockx', '', 1275, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 38, 'Alex Michelsen', '', 1205, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 39, 'Mariano Navone', '', 1165, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 40, 'Tallon Griekspoor', '', 1165, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 41, 'Denis Shapovalov', '', 1130, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 42, 'Tomas Machac', '', 1120, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 43, 'Jaume Munar', '', 1105, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 44, 'Adrian Mannarino', '', 1099, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 45, 'Juan Manuel Cerundolo', '', 1055, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 46, 'Marin Cilic', '', 1036, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 47, 'Kamil Majchrzak', '', 1032, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 48, 'Zizou Bergs', '', 1010, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 49, 'Matteo Berrettini', '', 985, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 50, 'Miomir Kecmanovic', '', 980, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 51, 'Raphael Collignon', '', 966, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 52, 'Thiago Agustin Tirante', '', 961, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 53, 'Terence Atmane', '', 948, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 54, 'Nuno Borges', '', 945, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 55, 'Martin Landaluce', '', 936, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 56, 'Botic van de Zandschulp', '', 935, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 57, 'Sebastian Baez', '', 935, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 58, 'Camilo Ugo Carabelli', '', 930, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 59, 'Yannick Hanfmann', '', 915, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 60, 'Sebastian Korda', '', 900, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 61, 'Fabian Marozsan', '', 895, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 62, 'Roman Andres Burruchaga', '', 875, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 63, 'Holger Rune', '', 860, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 64, 'Vit Kopriva', '', 856, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 65, 'Lorenzo Sonego', '', 855, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 66, 'Ethan Quinn', '', 854, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 67, 'Hamad Medjedovic', '', 844, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 68, 'Aleksandar Kovacevic', '', 837, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 69, 'Zachary Svajda', '', 835, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 70, 'Pablo Carreno Busta', '', 834, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 71, 'Adolfo Daniel Vallejo', '', 828, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 72, 'Jesper de Jong', '', 821, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 73, 'Jenson Brooksby', '', 817, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 74, 'Mattia Bellucci', '', 814, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 75, 'Valentin Royer', '', 798, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 76, 'Marton Fucsovics', '', 781, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 77, 'Jan-Lennard Struff', '', 779, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 78, 'James Duckworth', '', 776, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 79, 'Arthur Cazaux', '', 757, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 80, 'Stefanos Tsitsipas', '', 740, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 81, 'Daniel Altmaier', '', 740, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 82, 'Dino Prizmic', '', 735, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 83, 'Daniel Merida', '', 714, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 84, 'Gabriel Diallo', '', 710, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 85, 'Emilio Nava', '', 707, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 86, 'Marcos Giron', '', 705, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 87, 'Giovanni Mpetshi Perricard', '', 695, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 88, 'Francisco Comesana', '', 688, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 89, 'Benjamin Bonzi', '', 682, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 90, 'Alexei Popyrin', '', 680, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 91, 'Adam Walton', '', 679, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 92, 'Marco Trungelliti', '', 670, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 93, 'Jaime Faria', '', 668, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 94, 'Quentin Halys', '', 666, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 95, 'Eliot Spizzirri', '', 662, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 96, 'Aleksandr Shevchenko', '', 658, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 97, 'Sho Shimabukuro', '', 651, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 98, 'Aleksandar Vukic', '', 626, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 99, 'Yibing Wu', '', 626, 7.0, 'ATP', NULL, 0),
(UUID(), @new_snapshot_id, 'men', 100, 'Luca Van Assche', '', 626, 7.0, 'ATP', NULL, 0);

INSERT IGNORE INTO `ir_profile`.`world_ranking_players`
(`id`, `snapshot_id`, `gender`, `rank_no`, `name`, `country`, `points`, `ntrp`, `source`, `avatar_url`, `status`)
SELECT UUID(),
       @new_snapshot_id,
       `gender`,
       `rank_no`,
       `name`,
       `country`,
       `points`,
       `ntrp`,
       `source`,
       `avatar_url`,
       0
FROM `ir_profile`.`world_ranking_players`
WHERE `snapshot_id` = @previous_snapshot_id
  AND `gender` = 'women'
  AND `status` = 0;

INSERT IGNORE INTO `ir_profile`.`city_ranking_players`
(`id`, `snapshot_id`, `rank_no`, `name`, `city`, `level`, `ntrp`, `utr`, `matches`, `group_wins`, `avatar_url`, `status`)
SELECT UUID(),
       @new_snapshot_id,
       `rank_no`,
       `name`,
       `city`,
       `level`,
       `ntrp`,
       `utr`,
       `matches`,
       `group_wins`,
       `avatar_url`,
       0
FROM `ir_profile`.`city_ranking_players`
WHERE `snapshot_id` = @previous_snapshot_id
  AND `status` = 0;
