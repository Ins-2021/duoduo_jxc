package com.duoduo.jxc.dto.sales;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuotationQuery extends PageQuery {
    private String quotationNo;
    private Long customerId;
    private String customerName;
    private String quotationStatus;
    private String keyword;
}
