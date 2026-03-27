USE duoduo_jxc;

ALTER DATABASE duoduo_jxc CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

ALTER TABLE jxc_sys_user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE jxc_sys_config CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE jxc_print_template CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
