package com.duoduo.jxc.controller.app;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.BundleDTO;
import com.duoduo.jxc.dto.BundleQuery;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.service.BundleService;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-组长端接口
 */
@RestController
@RequestMapping("/leader")
@RequiredArgsConstructor
public class LeaderController {

    private final BundleService bundleService;
    private final ProductionOrderService productionOrderService;
    private final SysUserMapper sysUserMapper;

    /**
     * 获取概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalTasks", 0);
        overview.put("pendingTasks", 0);
        overview.put("completedTasks", 0);
        return Result.success(overview);
    }

    /**
     * 获取待分配扎包列表
     */
    @GetMapping("/bundle/pending")
    public Result<List<BundleDTO>> getPendingBundles() {
        var query = new BundleQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        return Result.success(bundleService.pageQuery(query).getList());
    }

    /**
     * 获取工人列表
     */
    @GetMapping("/worker/list")
    public Result<List<SysUser>> getWorkerList() {
        return Result.success(sysUserMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 1)
                        .eq(SysUser::getDeleted, 0)));
    }

    /**
     * 分配任务
     */
    @PostMapping("/assign")
    public Result<Void> assignTask(@RequestBody Map<String, Object> params) {
        // 任务分配逻辑
        return Result.success();
    }

    /**
     * 获取进度列表
     */
    @GetMapping("/progress/list")
    public Result<PageResult<ProductionOrderDTO>> getProgressList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var query = new ProductionOrderQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(productionOrderService.pageQuery(query));
    }

    /**
     * 获取异常列表
     */
    @GetMapping("/exception/list")
    public Result<List<Map<String, Object>>> getExceptionList() {
        return Result.success(List.of());
    }

    /**
     * 处理异常
     */
    @PostMapping("/exception/handle")
    public Result<Void> handleException(@RequestBody Map<String, Object> params) {
        return Result.success();
    }

    /**
     * 获取审批列表
     */
    @GetMapping("/approval/list")
    public Result<List<Map<String, Object>>> getApprovalList() {
        return Result.success(List.of());
    }

    /**
     * 审批操作
     */
    @PostMapping("/approval")
    public Result<Void> approve(@RequestBody Map<String, Object> params) {
        return Result.success();
    }
}
