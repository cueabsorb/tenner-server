CREATE DATABASE IF NOT EXISTS `ir_message` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ir_message`;

CREATE TABLE IF NOT EXISTS `app_messages` (
    `id`                    CHAR(36)        NOT NULL COMMENT 'UUID v4',
    `message_type`          ENUM('chat','system') NOT NULL COMMENT '消息类型：聊天消息/系统推送消息',
    `sender_id`             CHAR(36)        NULL     COMMENT '发送用户ID；系统消息为空',
    `recipient_id`          CHAR(36)        NOT NULL COMMENT '接收用户ID',
    `title`                 VARCHAR(120)    NULL     COMMENT '消息标题，系统推送常用',
    `content`               VARCHAR(500)    NOT NULL COMMENT '消息正文',
    `related_type`          VARCHAR(40)     NULL     COMMENT '关联业务类型',
    `related_id`            CHAR(36)        NULL     COMMENT '关联业务ID',
    `read_at`               DATETIME        NULL     COMMENT '接收方读取时间',
    `status`                TINYINT         NOT NULL DEFAULT 0 COMMENT '数据状态: 0=正常, -1=删除',
    `created_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_recipient_created` (`recipient_id`, `created_at` DESC),
    KEY `idx_recipient_unread` (`recipient_id`, `message_type`, `read_at`, `created_at` DESC),
    KEY `idx_chat_pair_created` (`sender_id`, `recipient_id`, `created_at` DESC),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='APP站内消息与推送消息';
