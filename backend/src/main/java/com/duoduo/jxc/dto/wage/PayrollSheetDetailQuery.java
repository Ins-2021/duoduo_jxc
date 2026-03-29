package com.duoduo.jxc.dto.wage;

import lombok.Data;

@Data
public class PayrollSheetDetailQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long sheetId;
    private String sheetNo;
    private String yearMonth;
    private Long employeeId;
    private String employeeName;
    private Long departmentId;
    private String costType;
    /** 发放状态: 0=pending, 1=paid, 2=failed */
    private Integer payStatus;
}
