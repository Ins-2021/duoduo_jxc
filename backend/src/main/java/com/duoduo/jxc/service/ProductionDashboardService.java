package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.production.ProductionOrderCard;
import com.duoduo.jxc.dto.production.ProductionOrdersResult;
import com.duoduo.jxc.dto.production.ProductionStatistics;

import java.util.List;

/**
 * 生产看板服务
 */
public interface ProductionDashboardService {

    /**
     * 获取统计数据
     *
     * @param factoryId 工厂ID
     * @return 统计数据
     */
    ProductionStatistics getStatistics(Long factoryId);

    /**
     * 获取订单列表
     *
     * @param factoryId 工厂ID
     * @return 订单列表
     */
    ProductionOrdersResult getOrders(Long factoryId);

    /**
     * 获取待开工订单
     *
     * @param factoryId 工厂ID
     * @return 订单列表
     */
    List<ProductionOrderCard> getPendingOrders(Long factoryId);

    /**
     * 获取生产中订单
     *
     * @param factoryId 工厂ID
     * @return 订单列表
     */
    List<ProductionOrderCard> getInProgressOrders(Long factoryId);

    /**
     * 获取已完成订单
     *
     * @param factoryId 工厂ID
     * @param days      查询天数（默认30天）
     * @return 订单列表
     */
    List<ProductionOrderCard> getCompletedOrders(Long factoryId, Integer days);
}
