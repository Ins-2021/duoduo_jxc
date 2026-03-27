package com.duoduo.jxc.dto.report;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class SalesReportQuery extends PageQuery {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long customerId;
    private Long spuId;
}
