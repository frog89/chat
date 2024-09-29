DROP table `chat_session`;
DROP table `chat_message`;
DROP table `chat_user`;

CREATE TABLE `chat_user` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`chat_user_kind` VARCHAR(10) NOT NULL,
	`chat_user_role` VARCHAR(10) NOT NULL,
	`name` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `chat_session` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`chat_user_id` BIGINT(20) UNSIGNED NOT NULL,
	`chat_user_ip` VARCHAR(30) NOT NULL,
	`start_time` DATETIME NOT NULL,
	`end_time` DATETIME,
	`status` VARCHAR(15),
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `chat_message` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`from_chat_user_id` BIGINT(20) UNSIGNED NOT NULL,
	`from_chat_user_session_id` BIGINT(20) UNSIGNED NOT NULL,
	`to_chat_user_id` BIGINT(20) UNSIGNED NOT NULL,
	`to_chat_user_session_id` BIGINT(20) UNSIGNED NOT NULL,
	`datetime` DATETIME NOT NULL,
	`message` TEXT NOT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;
