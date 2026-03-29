package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryExportDTO {

    @ExcelProperty("商品编码")
    @ColumnWidth(20)
    private String productCode;

    @ExcelProperty("商品名称")
    @ColumnWidth(25)
    private String productName;

    @ExcelProperty("规格")
    @ColumnWidth(15)
    private String spec;

    @ExcelProperty("仓库")
    @ColumnWidth(15)
    private String warehouseName;

    @ExcelProperty("库存数量")
    @ColumnWidth(12)
    private Integer qty;

    @ExcelProperty("可用库存")
    @ColumnWidth(12)
    private Integer availableQty;

    @ExcelProperty("锁定库存")
    @ColumnWidth(12)
    private Integer lockedQty;

    @ExcelProperty("成本价")
    @ColumnWidth(12)
    private BigDecimal costPrice;
}
