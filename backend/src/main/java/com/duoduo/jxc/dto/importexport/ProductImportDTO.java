package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ProductImportDTO {

    @ExcelProperty(value = "商品编码", index = 0)
    private String productCode;

    @ExcelProperty(value = "商品名称", index = 1)
    private String productName;

    @ExcelProperty(value = "商品类型", index = 2)
    private String productType;

    @ExcelProperty(value = "分类名称", index = 3)
    private String categoryName;

    @ExcelProperty(value = "单位", index = 4)
    private String unit;

    @ExcelProperty(value = "成本价", index = 5)
    private String costPrice;

    @ExcelProperty(value = "销售价", index = 6)
    private String salePrice;

    @ExcelProperty(value = "条码", index = 7)
    private String barcode;

    @ExcelProperty(value = "状态", index = 8)
    private String status;

    private Integer rowNum;

    private String errorMsg;
}
