package com.duoduo.jxc.dto.wage;

import lombok.Data;

@Data
public class PayrollSheetQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String sheetNo;
    private String yearMonth;
    private Long departmentId;
    private Integer status;
}
