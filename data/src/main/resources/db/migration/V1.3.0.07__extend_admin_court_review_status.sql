-- ============================================================
-- IRallyIn: support PC admin court review states.
-- ============================================================

ALTER TABLE `ir_profile`.`courts`
    MODIFY COLUMN `approval_status` ENUM('pending','approved','rejected','voided','blacklisted')
        NOT NULL DEFAULT 'approved' COMMENT '平台审核状态';
