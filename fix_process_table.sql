ALTER TABLE jxc_process 
  CHANGE COLUMN process_no process_code VARCHAR(32),
  CHANGE COLUMN is_active status INT DEFAULT 1,
  ADD COLUMN IF NOT EXISTS process_type VARCHAR(20),
  ADD COLUMN IF NOT EXISTS standard_price DECIMAL(10,2);
