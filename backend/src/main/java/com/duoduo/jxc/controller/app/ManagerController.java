package com.duoduo.jxc.controller.app;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.dto.production.ProductionOrdersResult;
import com.duoduo.jxc.dto.production.ProductionStatistics;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.service.ProductionDashboardService;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-厂长/经理端接口
 */
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ProductionOrderService productionOrderService;
    private final ProductionDashboardService productionDashboardService;
    private final SysUserMapper sysUserMapper;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        ProductionStatistics statistics = productionDashboardService.getStatistics(null);
        ProductionOrdersResult orders = productionDashboardService.getOrders(null);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("producingOrders", statistics.getInProgressCount());
        dashboard.put("todayOutput", statistics.getTodayOutput());
        dashboard.put("weekOutput", statistics.getWeekOutput());
        dashboard.put("passRate", statistics.getQualityPassRate() != null ? statistics.getQualityPassRate() + "%" : "0");
        dashboard.put("pendingOrders", orders.getPending());
        dashboard.put("producingOrdersList", orders.getInProgress());
        dashboard.put("completedOrdersList", orders.getCompleted());
        return Result.success(dashboard);
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/order/list")
    public Result<PageResult<ProductionOrderDTO>> getOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var query = new ProductionOrderQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(productionOrderService.pageQuery(query));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/order/{id}")
    public Result<ProductionOrderDTO> getOrderDetail(@PathVariable Long id) {
        return Result.success(productionOrderService.getDetail(id));
    }

    /**
     * 获取员工列表
     */
    @GetMapping("/staff/list")
    public Result<List<SysUser>> getStaffList() {
        return Result.success(sysUserMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 1)
                        .eq(SysUser::getDeleted, 0)));
    }

    /**
     * 获取报表列表
     */
    @GetMapping("/report/list")
    public Result<List<Map<String, Object>>> getReports() {
        ProductionStatistics statistics = productionDashboardService.getStatistics(null);
        List<Map<String, Object>> reports = List.of(
                Map.of("id", 1, "name", "生产日报", "type", "daily",
                        "date", LocalDate.now().toString(),
                        "content", "今日产量" + (statistics.getTodayOutput() != null ? statistics.getTodayOutput() : 0) + "件，合格率" + (statistics.getQualityPassRate() != null ? statistics.getQualityPassRate() : 0) + "%"),
                Map.of("id", 2, "name", "生产周报", "type", "weekly",
                        "date", LocalDate.now().minusWeeks(1).toString(),
                        "content", "本周产量" + (statistics.getWeekOutput() != null ? statistics.getWeekOutput() : 0) + "件"),
                Map.of("id", 3, "name", "质量报表", "type", "quality",
                        "date", LocalDate.now().toString(),
                        "content", "合格率" + (statistics.getQualityPassRate() != null ? statistics.getQualityPassRate() : 0) + "%")
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
        report.put("todayOutput", statistics.getTodayOutput());
        report.put("weekOutput", statistics.getWeekOutput());
        report.put("passRate", statistics.getQualityPassRate());
        report.put("inProgressCount", statistics.getInProgressCount());
        return Result.success(report);
    }
}
