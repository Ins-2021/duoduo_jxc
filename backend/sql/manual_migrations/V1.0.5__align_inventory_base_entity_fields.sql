ALTER TABLE jxc_inventory
    ADD COLUMN IF NOT EXISTS create_by BIGINT COMMENT '创建人ID' AFTER update_time,
    ADD COLUMN IF NOT EXISTS update_by BIGINT COMMENT '更新人ID' AFTER create_by,
    ADD COLUMN IF NOT EXISTS deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记' AFTER update_by;

UPDATE jxc_inventory SET deleted = 0 WHERE deleted IS NULL;
