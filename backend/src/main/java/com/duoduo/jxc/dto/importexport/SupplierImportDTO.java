package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SupplierImportDTO {

    @ExcelProperty(value = "供应商编码", index = 0)
    private String supplierCode;

    @ExcelProperty(value = "供应商名称", index = 1)
    private String supplierName;

    @ExcelProperty(value = "联系人", index = 2)
    private String contactName;

    @ExcelProperty(value = "电话", index = 3)
    private String phone;

    @ExcelProperty(value = "地址", index = 4)
    private String address;

    @ExcelProperty(value = "状态", index = 5)
    private String status;

    private Integer rowNum;

    private String errorMsg;
}
