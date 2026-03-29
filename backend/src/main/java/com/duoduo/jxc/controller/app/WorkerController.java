package com.duoduo.jxc.controller.app;

import com.duoduo.jxc.dto.ProcessQuery;
import com.duoduo.jxc.dto.ProcessDTO;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.wage.PieceRecordDTO;
import com.duoduo.jxc.dto.wage.PieceRecordQuery;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.service.BundleService;
import com.duoduo.jxc.service.PieceRecordService;
import com.duoduo.jxc.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-工人端接口
 */
@RestController
@RequestMapping("/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final PieceRecordService pieceRecordService;
    private final ProcessService processService;
    private final BundleService bundleService;

    /**
     * 扫码获取扎包信息
     */
    @GetMapping("/bundle/info")
    public Result<Bundle> getBundleInfo(@RequestParam String bundleNo) {
        return Result.success(bundleService.lambdaQuery()
                .eq(Bundle::getBundleNo, bundleNo)
                .one());
    }

    /**
     * 获取工序列表
     */
    @GetMapping("/process/list")
    public Result<?> getProcessList(@RequestParam(required = false) String processCode) {
        var query = new com.duoduo.jxc.dto.ProcessQuery();
        query.setProcessCode(processCode);
        query.setPageNum(1);
        query.setPageSize(200);
        return Result.success(processService.pageQuery(query).getList());
    }

    /**
     * 提交计件记录
     */
    @PostMapping("/piecework")
    public Result<Long> submitPiecework(@RequestBody PieceRecordDTO dto) {
        return Result.success(pieceRecordService.create(dto));
    }

    /**
     * 获取今日计件记录
     */
    @GetMapping("/piecework/today")
    public Result<List<PieceRecordDTO>> getTodayRecords() {
        var query = new PieceRecordQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        query.setRecordDateFrom(LocalDate.now());
        query.setRecordDateTo(LocalDate.now());
        var page = pieceRecordService.pageQuery(query);
        return Result.success(page.getList());
    }

    /**
     * 获取工资汇总
     */
    @GetMapping("/wage/summary")
    public Result<Map<String, Object>> getWageSummary(@RequestParam String month) {
        LocalDate from = LocalDate.parse(month + "-01");
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        var query = new PieceRecordQuery();
        query.setRecordDateFrom(from);
        query.setRecordDateTo(to);
        List<Map<String, Object>> summaryList = pieceRecordService.summary(query);
        Map<String, Object> result = new HashMap<>();
        result.put("month", month);
        result.put("records", summaryList);
        return Result.success(result);
    }

    /**
     * 获取工资明细
     */
    @GetMapping("/wage/detail")
    public Result<PageResult<PieceRecordDTO>> getWageDetail(
            @RequestParam String month,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        LocalDate from = LocalDate.parse(month + "-01");
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        var query = new PieceRecordQuery();
        query.setRecordDateFrom(from);
        query.setRecordDateTo(to);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(pieceRecordService.pageQuery(query));
    }

    /**
     * 获取我的任务列表
     */
    @GetMapping("/task/list")
    public Result<PageResult<PieceRecordDTO>> getMyTasks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Integer status) {
        var query = new PieceRecordQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        if (status != null) {
            query.setAuditStatus(status);
        }
        return Result.success(pieceRecordService.pageQuery(query));
    }
}
