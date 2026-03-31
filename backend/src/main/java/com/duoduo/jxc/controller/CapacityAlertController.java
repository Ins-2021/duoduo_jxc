package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.report.CapacityAlertDTO;
import com.duoduo.jxc.dto.report.CapacityStatusDTO;
import com.duoduo.jxc.dto.report.DelayRiskDTO;
import com.duoduo.jxc.service.CapacityAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产能预警控制器
 */
@RestController
@RequestMapping("/capacity-alert")
@RequiredArgsConstructor
public class CapacityAlertController {

    private final CapacityAlertService capacityAlertService;

    /**
     * 获取活跃预警
     */
    @GetMapping("/alerts")
    public Result<List<CapacityAlertDTO>> getActiveAlerts(@RequestParam(required = false) Long factoryId) {
        return Result.success(capacityAlertService.getActiveAlerts(factoryId));
    }

    /**
     * 确认预警
     */
    @PostMapping("/alerts/{alertId}/acknowledge")
    public Result<Void> acknowledgeAlert(@PathVariable Long alertId, @RequestParam Long userId) {
        capacityAlertService.acknowledgeAlert(alertId, userId);
        return Result.success();
    }

    /**
     * 解决预警
     */
    @PostMapping("/alerts/{alertId}/resolve")
    public Result<Void> resolveAlert(@PathVariable Long alertId, @RequestParam Long userId, @RequestParam String resolution) {
        capacityAlertService.resolveAlert(alertId, userId, resolution);
        return Result.success();
    }

    /**
     * 获取工序产能状态
     */
    @GetMapping("/capacity-status")
    public Result<List<CapacityStatusDTO>> getProcessCapacityStatus(@RequestParam(required = false) Long factoryId) {
        return Result.success(capacityAlertService.getProcessCapacityStatus(factoryId));
    }

    /**
     * 获取延期风险订单
     */
    @GetMapping("/delay-risks")
    public Result<List<DelayRiskDTO>> getDelayRiskOrders(@RequestParam(required = false) Long factoryId) {
        return Result.success(capacityAlertService.getDelayRiskOrders(factoryId));
    }
}
