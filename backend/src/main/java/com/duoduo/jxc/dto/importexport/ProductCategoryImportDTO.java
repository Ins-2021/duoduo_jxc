package com.duoduo.jxc.dto.importexport;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ProductCategoryImportDTO {
    @ExcelProperty("分类名称")
    private String categoryName;
    
    @ExcelProperty("上级分类")
    private String parentName;
    
    @ExcelProperty("排序")
    private Integer sort = 0;
    
    // 错误信息
    private String errorMsg;
}
