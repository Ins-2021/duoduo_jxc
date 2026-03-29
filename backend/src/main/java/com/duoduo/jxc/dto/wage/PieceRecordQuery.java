package com.duoduo.jxc.dto.wage;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PieceRecordQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String recordNo;
    private LocalDate recordDateFrom;
    private LocalDate recordDateTo;
    private Long productionId;
    private Long styleId;
    private Long employeeId;
    private String employeeName;
    private String processCode;
    private Long workshopId;
    /** 审核状态: 0=pending, 1=approved, 2=rejected */
    private Integer auditStatus;
}
