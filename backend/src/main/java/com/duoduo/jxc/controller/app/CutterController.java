package com.duoduo.jxc.controller.app;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.CutBundleDTO;
import com.duoduo.jxc.dto.production.CutOrderDTO;
import com.duoduo.jxc.dto.production.CutOrderQuery;
import com.duoduo.jxc.service.CutBundleService;
import com.duoduo.jxc.service.CutOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-裁剪工端接口
 */
@RestController
@RequestMapping("/cutter")
@RequiredArgsConstructor
public class CutterController {

    private final CutOrderService cutOrderService;
    private final CutBundleService cutBundleService;

    /**
     * 获取裁剪任务列表
     */
    @GetMapping("/task/list")
    public Result<PageResult<CutOrderDTO>> getCuttingTasks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var query = new CutOrderQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(cutOrderService.pageQuery(query));
    }

    /**
     * 获取裁剪任务详情
     */
    @GetMapping("/task/{id}")
    public Result<CutOrderDTO> getCuttingTaskDetail(@PathVariable Long id) {
        return Result.success(cutOrderService.getDetail(id));
    }

    /**
     * 提交裁剪录入
     */
    @PostMapping("/input")
    public Result<Void> submitCuttingInput(@RequestBody CutOrderDTO dto) {
        cutOrderService.update(dto);
        return Result.success();
    }

    /**
     * 获取待裁剪订单
     */
    @GetMapping("/order/pending")
    public Result<List<CutOrderDTO>> getPendingOrders() {
        var query = new CutOrderQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        var page = cutOrderService.pageQuery(query);
        return Result.success(page.getList());
    }

    /**
     * 生成扎包
     */
    @PostMapping("/bundle/generate")
    public Result<Long> generateBundles(@RequestBody CutBundleDTO dto) {
        return Result.success(cutBundleService.create(dto));
    }

    /**
     * 获取扎包详情
     */
    @GetMapping("/bundle/{id}")
    public Result<CutBundleDTO> getBundleDetail(@PathVariable Long id) {
        return Result.success(cutBundleService.getDetail(id));
    }

    /**
     * 打印扎包标签
     */
    @PostMapping("/bundle/{id}/print")
    public Result<Void> printBundleLabel(@PathVariable Long id) {
        // 调用条码打印服务
        return Result.success();
    }

    /**
     * 获取今日统计
     */
    @GetMapping("/stats/today")
    public Result<Map<String, Object>> getTodayStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("date", LocalDate.now().toString());
        stats.put("cutQuantity", 0);
        stats.put("bundleCount", 0);
        stats.put("pendingOrders", 0);
        return Result.success(stats);
    }

    /**
     * 获取今日记录
     */
    @GetMapping("/records/today")
    public Result<List<CutBundleDTO>> getTodayRecords() {
        return Result.success(List.of());
    }
}
