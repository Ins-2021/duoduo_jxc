package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.ProductionOrdersResult;
import com.duoduo.jxc.dto.production.ProductionStatistics;
import com.duoduo.jxc.service.ProductionDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生产看板控制器
 */
@RestController
@RequestMapping("/api/production/dashboard")
public class ProductionDashboardController {

    @Autowired
    private ProductionDashboardService dashboardService;

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<ProductionStatistics> getStatistics(
            @RequestParam(required = false) Long factoryId) {
        return Result.success(dashboardService.getStatistics(factoryId));
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/orders")
    public Result<ProductionOrdersResult> getOrders(
            @RequestParam(required = false) Long factoryId) {
        return Result.success(dashboardService.getOrders(factoryId));
    }
}
