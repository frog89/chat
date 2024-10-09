DROP table if exists `chat_session`;
DROP table if exists `chat_message`;
DROP table if exists `chat`;

CREATE TABLE `chat_session` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  	`chat_user_name` TEXT NOT NULL COLLATE 'utf8mb4_general_ci',
	`chat_user_kind` ENUM('UNKNOWN', 'MALE', 'FEMALE', 'COUPLE') NOT NULL,
    `chat_user_role` ENUM('USER', 'ADMIN') NOT NULL,
	`pwd_hash` TEXT NOT NULL COLLATE 'utf8mb4_general_ci',
	`start_time` DATETIME NOT NULL,
	`end_time` DATETIME,
	`status` ENUM('ACTIVE', 'ENDED_LOGOUT', 'ENDED_TIMEOUT') NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `chat` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`first_chat_session_id` BIGINT(20) UNSIGNED NOT NULL,
	`second_chat_session_id` BIGINT(20) UNSIGNED NOT NULL,
	`is_hidden_for_first_chat_session` ENUM('Y','N') NULL DEFAULT 'N',
	`is_hidden_for_second_chat_session` ENUM('Y','N') NULL DEFAULT 'N',
	`last_seen_message_id` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0,
	`start_datetime` DATETIME NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `chat_message` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`chat_id` BIGINT(20) UNSIGNED NOT NULL,
	`from_chat_session_id` BIGINT(20) UNSIGNED NOT NULL,
	`datetime` DATETIME NOT NULL,
	`message` TEXT NOT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;
