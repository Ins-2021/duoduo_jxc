USE duoduo_jxc;
SET NAMES utf8mb4;

INSERT INTO jxc_print_template
(`template_name`,`biz_type`,`paper_type`,`paper_width_mm`,`paper_height_mm`,`design_json`,`is_default`,`enabled`)
VALUES
('标签30*20','标签','30 * 20mm',30,20,NULL,1,1),
('销售单','销售','二等分',210,140,NULL,0,1),
('资金调整单','资金调整','二等分',210,140,NULL,1,1),
('零售退货单','零售退货','小票（58mm）',58,160,NULL,0,1);
