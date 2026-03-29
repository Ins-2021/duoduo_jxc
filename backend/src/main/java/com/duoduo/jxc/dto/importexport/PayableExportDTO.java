package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayableExportDTO {

    @ExcelProperty("单据编号")
    @ColumnWidth(20)
    private String billNo;

    @ExcelProperty("供应商名称")
    @ColumnWidth(20)
    private String supplierName;

    @ExcelProperty("应付金额")
    @ColumnWidth(15)
    private BigDecimal amount;

    @ExcelProperty("已付金额")
    @ColumnWidth(15)
    private BigDecimal paidAmount;

    @ExcelProperty("剩余金额")
    @ColumnWidth(15)
    private BigDecimal remainingAmount;

    @ExcelProperty("单据日期")
    @ColumnWidth(15)
    private String billDate;

    @ExcelProperty("到期日期")
    @ColumnWidth(15)
    private String dueDate;

    @ExcelProperty("状态")
    @ColumnWidth(12)
    private String statusText;
}
