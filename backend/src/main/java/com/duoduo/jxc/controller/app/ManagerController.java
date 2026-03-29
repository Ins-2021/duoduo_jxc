package com.duoduo.jxc.controller.app;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    private final SysUserMapper sysUserMapper;

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
