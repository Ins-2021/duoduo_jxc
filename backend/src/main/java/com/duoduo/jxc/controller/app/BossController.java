package com.duoduo.jxc.controller.app;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-老板/老板娘端接口
 */
@RestController
@RequestMapping("/boss")
@RequiredArgsConstructor
public class BossController {

    private final ProductionOrderService productionOrderService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("producingOrders", 0);
        dashboard.put("todayOutput", 0);
        dashboard.put("weekOutput", 0);
        dashboard.put("passRate", "0");
        return Result.success(dashboard);
    }

    /**
     * 获取KPI数据
     */
    @GetMapping("/kpi")
    public Result<Map<String, Object>> getKpi() {
        Map<String, Object> kpi = new HashMap<>();
        kpi.put("monthOutput", 0);
        kpi.put("monthOutputGrowth", 0);
        kpi.put("monthRevenue", 0);
        kpi.put("monthRevenueGrowth", 0);
        kpi.put("passRate", 0);
        kpi.put("onTimeRate", 0);
        return Result.success(kpi);
    }

    /**
     * 获取生产进度
     */
    @GetMapping("/production/progress")
    public Result<List<ProductionOrderDTO>> getProductionProgress() {
        var query = new ProductionOrderQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        return Result.success(productionOrderService.pageQuery(query).getList());
    }

    /**
     * 获取预警列表
     */
    @GetMapping("/alert/list")
    public Result<List<Map<String, Object>>> getAlerts() {
        return Result.success(List.of());
    }

    /**
     * 获取报表列表
     */
    @GetMapping("/report/list")
    public Result<List<Map<String, Object>>> getReports() {
        return Result.success(List.of());
    }

    /**
     * 获取报表详情
     */
    @GetMapping("/report/{id}")
    public Result<Map<String, Object>> getReportDetail(@PathVariable Long id) {
        Map<String, Object> report = new HashMap<>();
        report.put("id", id);
        return Result.success(report);
    }
}
