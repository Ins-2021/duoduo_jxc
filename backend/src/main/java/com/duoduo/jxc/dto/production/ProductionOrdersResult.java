package com.duoduo.jxc.dto.production;

import lombok.Data;
import java.util.List;

/**
 * 生产订单列表结果
 */
@Data
public class ProductionOrdersResult {
    /**
     * 待开工订单
     */
    private List<ProductionOrderCard> pending;
    
    /**
     * 生产中订单
     */
    private List<ProductionOrderCard> inProgress;
    
    /**
     * 已完成订单
     */
    private List<ProductionOrderCard> completed;
}
