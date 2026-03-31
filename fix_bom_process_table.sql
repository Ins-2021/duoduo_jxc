ALTER TABLE jxc_bom_process
  CHANGE COLUMN bom_process_id id BIGINT AUTO_INCREMENT,
  ADD COLUMN IF NOT EXISTS process_code VARCHAR(50),
  ADD COLUMN IF NOT EXISTS process_name VARCHAR(100),
  CHANGE COLUMN standard_minutes standard_time DECIMAL(10,2),
  ADD COLUMN IF NOT EXISTS piece_price DECIMAL(10,2);
