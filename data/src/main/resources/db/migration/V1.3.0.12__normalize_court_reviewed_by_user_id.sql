-- Store reviewer as ir_auth.users.id, matching courts.created_by.
UPDATE `ir_profile`.`courts` c
JOIN `ir_auth`.`users` u
  ON LOWER(c.`reviewed_by`) = LOWER(u.`email`)
SET c.`reviewed_by` = u.`id`
WHERE c.`reviewed_by` IS NOT NULL
  AND TRIM(c.`reviewed_by`) <> ''
  AND c.`reviewed_by` LIKE '%@%';

UPDATE `ir_profile`.`court_change_requests` r
JOIN `ir_auth`.`users` u
  ON LOWER(r.`reviewed_by`) = LOWER(u.`email`)
SET r.`reviewed_by` = u.`id`
WHERE r.`reviewed_by` IS NOT NULL
  AND TRIM(r.`reviewed_by`) <> ''
  AND r.`reviewed_by` LIKE '%@%';
