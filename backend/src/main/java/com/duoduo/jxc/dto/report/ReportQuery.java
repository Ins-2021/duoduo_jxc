package com.duoduo.jxc.dto.report;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportQuery extends PageQuery {
    private LocalDate startDate;
    private LocalDate endDate;
    private YearMonth startMonth;
    private YearMonth endMonth;
    private Long customerId;
    private Long supplierId;
    private Long spuId;
    private Long workerId;
    private String orderBy;
    private Integer limit;
}
