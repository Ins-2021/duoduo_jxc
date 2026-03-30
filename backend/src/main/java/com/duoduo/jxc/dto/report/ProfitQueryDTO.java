package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 利润分析查询DTO
 */
@Data
public class ProfitQueryDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long customerId;
    private Long styleId;
    private String orderBy;      // profit/amount/quantity
    private Integer limit;       // 返回条数
}
