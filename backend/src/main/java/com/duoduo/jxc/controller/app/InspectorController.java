package com.duoduo.jxc.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.BundleDTO;
import com.duoduo.jxc.dto.BundleQuery;
import com.duoduo.jxc.dto.DefectRecordDTO;
import com.duoduo.jxc.dto.DefectRecordQuery;
import com.duoduo.jxc.dto.FirstArticleConfirmationDTO;
import com.duoduo.jxc.dto.FirstArticleConfirmationQuery;
import com.duoduo.jxc.dto.QualityCheckDTO;
import com.duoduo.jxc.dto.QualityCheckQuery;
import com.duoduo.jxc.entity.QualityCheck;
import com.duoduo.jxc.service.BundleService;
import com.duoduo.jxc.service.DefectRecordService;
import com.duoduo.jxc.service.FirstArticleConfirmationService;
import com.duoduo.jxc.service.QualityCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-质检员端接口
 */
@RestController
@RequestMapping("/inspector")
@RequiredArgsConstructor
public class InspectorController {

    private final BundleService bundleService;
    private final QualityCheckService qualityCheckService;
    private final FirstArticleConfirmationService firstArticleConfirmationService;
    private final DefectRecordService defectRecordService;

    /**
     * 扫码获取扎包信息
     */
    @GetMapping("/bundle/info")
    public Result<BundleDTO> getBundleInfo(@RequestParam String bundleNo) {
        var query = new BundleQuery();
        query.setBundleNo(bundleNo);
        query.setPageNum(1);
        query.setPageSize(1);
        PageResult<BundleDTO> page = bundleService.pageQuery(query);
        if (page.getList().isEmpty()) {
            return Result.error("扎包不存在");
        }
        return Result.success(page.getList().get(0));
    }

    /**
     * 提交质检
     */
    @PostMapping("/check")
    public Result<Long> submitQualityCheck(@RequestBody QualityCheckDTO dto) {
        return Result.success(qualityCheckService.create(dto));
    }

    /**
     * 获取今日统计
     */
    @GetMapping("/stats/today")
    public Result<Map<String, Object>> getTodayStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.atTime(LocalTime.MAX);

        long total = qualityCheckService.count(new LambdaQueryWrapper<QualityCheck>()
                .between(QualityCheck::getCreateTime, dayStart, dayEnd));
        long passed = qualityCheckService.count(new LambdaQueryWrapper<QualityCheck>()
                .between(QualityCheck::getCreateTime, dayStart, dayEnd)
                .eq(QualityCheck::getResult, "passed"));
        long failed = qualityCheckService.count(new LambdaQueryWrapper<QualityCheck>()
                .between(QualityCheck::getCreateTime, dayStart, dayEnd)
                .ne(QualityCheck::getResult, "passed"));

        String passRate = total > 0
                ? String.format("%.1f", passed * 100.0 / total)
                : "0";

        Map<String, Object> stats = new HashMap<>();
        stats.put("date", today.toString());
        stats.put("total", total);
        stats.put("passed", passed);
        stats.put("failed", failed);
        stats.put("passRate", passRate);
        return Result.success(stats);
    }

    /**
     * 获取待确认首件列表
     */
    @GetMapping("/first-article/pending")
    public Result<List<FirstArticleConfirmationDTO>> getPendingFirstArticles() {
        var query = new FirstArticleConfirmationQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        return Result.success(firstArticleConfirmationService.pageQuery(query).getList());
    }

    /**
     * 确认首件
     */
    @PostMapping("/first-article/{id}/confirm")
    public Result<Void> confirmFirstArticle(@PathVariable Long id, @RequestBody FirstArticleConfirmationDTO dto) {
        dto.setConfirmationId(id);
        firstArticleConfirmationService.update(dto);
        return Result.success();
    }

    /**
     * 获取返工列表
     */
    @GetMapping("/rework/list")
    public Result<PageResult<DefectRecordDTO>> getReworkList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var query = new DefectRecordQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(defectRecordService.pageQuery(query));
    }

    /**
     * 提交返工记录
     */
    @PostMapping("/rework")
    public Result<Long> submitRework(@RequestBody DefectRecordDTO dto) {
        return Result.success(defectRecordService.create(dto));
    }

    /**
     * 获取质检历史
     */
    @GetMapping("/history")
    public Result<PageResult<QualityCheckDTO>> getCheckHistory(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var query = new QualityCheckQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(qualityCheckService.pageQuery(query));
    }
}
