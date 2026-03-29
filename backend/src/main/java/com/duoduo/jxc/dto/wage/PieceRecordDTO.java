package com.duoduo.jxc.dto.wage;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PieceRecordDTO {
    private Long id;
    private String recordNo;
    private LocalDate recordDate;
    private Long productionId;
    private String productionNo;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private String processCode;
    private String processName;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private BigDecimal quantity;
    private BigDecimal qualifiedQuantity;
    private BigDecimal defectQuantity;
    private BigDecimal unitPrice;
    private BigDecimal wageAmount;
    private Long workshopId;
    private String workshopName;
    /** 审核状态: 0=pending, 1=approved, 2=rejected */
    private Integer auditStatus;
    private LocalDateTime auditTime;
    private Long auditBy;
    private String auditRemark;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
