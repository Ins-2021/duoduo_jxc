package com.duoduo.jxc.dto.production;

import lombok.Data;

/**
 * 生产统计数据
 */
@Data
public class ProductionStatistics {
    /**
     * 生产中订单数
     */
    private Integer inProgressCount;
    
    /**
     * 待开工订单数
     */
    private Integer pendingCount;
    
    /**
     * 今日产量
     */
    private Integer todayOutput;
    
    /**
     * 本周产量
     */
    private Integer weekOutput;
    
    /**
     * 本月产量
     */
    private Integer monthOutput;
    
    /**
     * 今日效率(%)
     */
    private java.math.BigDecimal todayEfficiency;
    
    /**
     * 延期订单数
     */
    private Integer delayedCount;
    
    /**
     * 质检合格率(%)
     */
    private Integer qualityPassRate;
}
