package com.duoduo.jxc.dto.home;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HomeOverviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal todayGrossProfit;
    private BigDecimal yesterdayGrossProfit;
    private BigDecimal todaySalesAmount;
    private BigDecimal yesterdaySalesAmount;
    private Integer todaySalesCount;
    private Integer yesterdaySalesCount;
    private BigDecimal todayCashIn;
    private BigDecimal yesterdayCashIn;

    private Long customerCount;
    private Long supplierCount;
    private Long inventoryQtyTotal;
    private BigDecimal inventoryAmountTotal;
    private Long inventoryWarnCount;
    private Long shelfLifeWarnCount;
    private BigDecimal receivableAmount;
    private BigDecimal payableAmount;
    private BigDecimal assetsTotal;

    private List<String> overviewDates;
    private List<Integer> overviewSalesOrderCount;

    private List<String> cashflowDates;
    private List<BigDecimal> cashflowIncome;
    private List<BigDecimal> cashflowExpense;
}

