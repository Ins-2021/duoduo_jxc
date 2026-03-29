package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceivableExportDTO {

    @ExcelProperty("单据编号")
    @ColumnWidth(20)
    private String billNo;

    @ExcelProperty("客户名称")
    @ColumnWidth(20)
    private String customerName;

    @ExcelProperty("应收金额")
    @ColumnWidth(15)
    private BigDecimal amount;

    @ExcelProperty("已收金额")
    @ColumnWidth(15)
    private BigDecimal receivedAmount;

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
