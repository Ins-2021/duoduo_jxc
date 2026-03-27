package com.duoduo.jxc.dto.retail;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class RetailSettlementQuery extends PageQuery {
    private Long storeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String settlementNo;
    private Integer status;
}