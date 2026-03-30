package com.duoduo.jxc.dto.production;

import lombok.Data;
import java.time.LocalDate;

/**
 * 生产订单卡片展示数据
 */
@Data
public class ProductionOrderCard {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 款号
     */
    private String styleCode;
    
    /**
     * 款式名称
     */
    private String styleName;
    
    /**
     * 总数量
     */
    private Integer totalQuantity;
    
    /**
     * 已完成数量
     */
    private Integer completedQuantity;
    
    /**
     * 进度百分比
     */
    private Integer progress;
    
    /**
     * 当前工序
     */
    private String currentProcess;
    
    /**
     * 优先级(low/medium/high/urgent)
     */
    private String priority;
    
    /**
     * 交期
     */
    private LocalDate deadline;
    
    /**
     * 延期天数
     */
    private Integer delayDays;
    
    /**
     * 状态
     */
    private String status;
}
