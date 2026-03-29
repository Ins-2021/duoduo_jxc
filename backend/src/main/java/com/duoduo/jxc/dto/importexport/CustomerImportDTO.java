package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerImportDTO {

    @ExcelProperty(value = "客户编码", index = 0)
    private String customerCode;

    @ExcelProperty(value = "客户名称", index = 1)
    private String customerName;

    @ExcelProperty(value = "客户类型", index = 2)
    private String customerType;

    @ExcelProperty(value = "联系人", index = 3)
    private String contactName;

    @ExcelProperty(value = "电话", index = 4)
    private String phone;

    @ExcelProperty(value = "地址", index = 5)
    private String address;

    @ExcelProperty(value = "销售员", index = 6)
    private String salesName;

    @ExcelProperty(value = "状态", index = 7)
    private String status;

    private Integer rowNum;

    private String errorMsg;
}
