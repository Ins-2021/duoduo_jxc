package com.duoduo.jxc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.BundleDTO;
import com.duoduo.jxc.dto.BundleQuery;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.entity.ProcessException;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.mapper.ProcessExceptionMapper;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.security.SecurityUtils;
import com.duoduo.jxc.service.BundleService;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final ProcessExceptionMapper processExceptionMapper;

    /**
     * 获取概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        var allQuery = new BundleQuery();
        allQuery.setPageNum(1);
        allQuery.setPageSize(1);
        long total = bundleService.pageQuery(allQuery).getTotal();

        var pendingQuery = new BundleQuery();
        pendingQuery.setPageNum(1);
        pendingQuery.setPageSize(1);
        // 待分配：status 为 pending 的扎包
        long pending = bundleService.count(new LambdaQueryWrapper<Bundle>()
                .eq(Bundle::getStatus, "pending")
                .eq(Bundle::getDeleted, 0));
        long completed = bundleService.count(new LambdaQueryWrapper<Bundle>()
                .eq(Bundle::getStatus, "completed")
                .eq(Bundle::getDeleted, 0));

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalTasks", total);
        overview.put("pendingTasks", pending);
        overview.put("completedTasks", completed);
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
        return Result.success(bundleService.pageQuery(query).getList().stream()
                .filter(b -> "pending".equals(b.getStatus()))
                .collect(Collectors.toList()));
    }

    /**
     * 获取工人列表
     */
    @GetMapping("/worker/list")
    public Result<List<SysUser>> getWorkerList() {
        return Result.success(sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 1)
                        .eq(SysUser::getDeleted, 0)));
    }

    /**
     * 分配任务
     */
    @PostMapping("/assign")
    public Result<Void> assignTask(@RequestBody Map<String, Object> params) {
        Long bundleId = Long.valueOf(params.get("bundleId").toString());
        // 更新扎包状态为 in_progress
        Bundle bundle = bundleService.getById(bundleId);
        if (bundle != null) {
            bundle.setStatus("in_progress");
            bundleService.updateById(bundle);
        }
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
        List<ProcessException> exceptions = processExceptionMapper.selectList(
                new LambdaQueryWrapper<ProcessException>()
                        .ne(ProcessException::getStatus, "resolved")
                        .orderByDesc(ProcessException::getCreateTime));
        List<Map<String, Object>> result = exceptions.stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("type", e.getExceptionType());
            m.put("description", e.getDescription());
            m.put("status", e.getStatus());
            m.put("createdAt", e.getCreateTime());
            return m;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 处理异常
     */
    @PostMapping("/exception/handle")
    public Result<Void> handleException(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String handlingMethod = params.get("handlingMethod").toString();
        String handlingResult = params.get("handlingResult").toString();

        ProcessException exception = processExceptionMapper.selectById(id);
        if (exception != null) {
            exception.setHandlingMethod(handlingMethod);
            exception.setHandlingResult(handlingResult);
            exception.setStatus("resolved");
            Long userId = SecurityUtils.getUserId();
            exception.setHandledBy(userId);
            exception.setHandledTime(LocalDateTime.now());
            processExceptionMapper.updateById(exception);
        }
        return Result.success();
    }

    /**
     * 获取审批列表（待首件确认的记录）
     */
    @GetMapping("/approval/list")
    public Result<List<Map<String, Object>>> getApprovalList() {
        // 暂时返回空列表，首件确认审批走 InspectorController
        return Result.success(List.of());
    }

    /**
     * 审批操作
     */
    @PostMapping("/approval")
    public Result<Void> approve(@RequestBody Map<String, Object> params) {
        // 暂无独立审批逻辑，首件确认走 InspectorController
        return Result.success();
    }
}
