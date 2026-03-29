package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesOrderExportDTO {

    @ExcelProperty("订单编号")
    @ColumnWidth(20)
    private String docNo;

    @ExcelProperty("订单日期")
    @ColumnWidth(15)
    private String docDate;

    @ExcelProperty("客户名称")
    @ColumnWidth(20)
    private String customerName;

    @ExcelProperty("订单金额")
    @ColumnWidth(15)
    private BigDecimal totalAmount;

    @ExcelProperty("已收金额")
    @ColumnWidth(15)
    private BigDecimal paidAmount;

    @ExcelProperty("订单状态")
    @ColumnWidth(12)
    private String statusText;

    @ExcelProperty("备注")
    @ColumnWidth(30)
    private String remark;
}
