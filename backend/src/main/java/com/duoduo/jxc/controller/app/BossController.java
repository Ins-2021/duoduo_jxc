package com.duoduo.jxc.controller.app;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.dto.production.ProductionStatistics;
import com.duoduo.jxc.dto.report.CapacityAlertDTO;
import com.duoduo.jxc.service.CapacityAlertService;
import com.duoduo.jxc.service.ProductionDashboardService;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小程序-老板/老板娘端接口
 */
@RestController
@RequestMapping("/boss")
@RequiredArgsConstructor
public class BossController {

    private final ProductionOrderService productionOrderService;
    private final ProductionDashboardService productionDashboardService;
    private final CapacityAlertService capacityAlertService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        ProductionStatistics statistics = productionDashboardService.getStatistics(null);
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("producingOrders", statistics.getInProgressCount());
        dashboard.put("todayOutput", statistics.getTodayOutput());
        dashboard.put("weekOutput", statistics.getWeekOutput());
        dashboard.put("passRate", statistics.getQualityPassRate() != null ? statistics.getQualityPassRate() + "%" : "0");
        return Result.success(dashboard);
    }

    /**
     * 获取KPI数据
     */
    @GetMapping("/kpi")
    public Result<Map<String, Object>> getKpi() {
        ProductionStatistics statistics = productionDashboardService.getStatistics(null);
        Map<String, Object> kpi = new HashMap<>();
        kpi.put("monthOutput", statistics.getMonthOutput());
        kpi.put("monthOutputGrowth", 0);
        kpi.put("monthRevenue", 0);
        kpi.put("monthRevenueGrowth", 0);
        kpi.put("passRate", statistics.getQualityPassRate());
        kpi.put("onTimeRate", statistics.getDelayedCount() != null && statistics.getInProgressCount() != null
                && statistics.getInProgressCount() > 0
                ? Math.round((statistics.getInProgressCount() - statistics.getDelayedCount()) * 100.0 / statistics.getInProgressCount() * 10) / 10.0
                : 0);
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
        try {
            List<CapacityAlertDTO> activeAlerts = capacityAlertService.getActiveAlerts(null);
            if (activeAlerts == null || activeAlerts.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            List<Map<String, Object>> alerts = activeAlerts.stream()
                    .filter(a -> a != null)
                    .map(a -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", a.getAlertId());
                        m.put("level", a.getAlertLevel());
                        m.put("title", a.getMessage());
                        m.put("description", a.getMessage());
                        m.put("createdAt", a.getCreatedAt());
                        return m;
                    }).collect(Collectors.toList());
            return Result.success(alerts);
        } catch (Exception e) {
            return Result.success(new ArrayList<>());
        }
    }

    /**
     * 获取报表列表
     */
    @GetMapping("/report/list")
    public Result<List<Map<String, Object>>> getReports() {
        List<Map<String, Object>> reports = List.of(
                Map.of("id", 1, "name", "生产日报", "type", "daily", "date", LocalDate.now().toString()),
                Map.of("id", 2, "name", "生产周报", "type", "weekly",
                        "date", LocalDate.now().minusWeeks(1).toString()),
                Map.of("id", 3, "name", "质量报表", "type", "quality", "date", LocalDate.now().toString())
        );
        return Result.success(reports);
    }

    /**
     * 获取报表详情
     */
    @GetMapping("/report/{id}")
    public Result<Map<String, Object>> getReportDetail(@PathVariable Long id) {
        ProductionStatistics statistics = productionDashboardService.getStatistics(null);
        Map<String, Object> report = new HashMap<>();
        report.put("id", id);
        report.put("name", switch (id.intValue()) {
            case 1 -> "生产日报";
            case 2 -> "生产周报";
            default -> "质量报表";
        });
        report.put("todayOutput", statistics.getTodayOutput());
        report.put("weekOutput", statistics.getWeekOutput());
        report.put("passRate", statistics.getQualityPassRate());
        report.put("inProgressCount", statistics.getInProgressCount());
        return Result.success(report);
    }
}
