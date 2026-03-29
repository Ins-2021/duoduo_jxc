package com.duoduo.jxc.controller.report;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.report.*;
import com.duoduo.jxc.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ========== 销售报表 ==========

    @Log(title = "报表管理", action = "查询销售日报")
    @GetMapping("/sales/daily")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:sales:view')")
    public Result<PageResult<SalesDailyReportDTO>> salesDailyReport(SalesReportQuery query) {
        return Result.success(reportService.getSalesDailyReport(query));
    }

    @Log(title = "报表管理", action = "查询销售月报")
    @GetMapping("/sales/monthly")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:sales:view')")
    public Result<PageResult<SalesMonthlyReportDTO>> salesMonthlyReport(ReportQuery query) {
        return Result.success(reportService.getSalesMonthlyReport(query));
    }

    @Log(title = "报表管理", action = "查询销售排行")
    @GetMapping("/sales/ranking")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:sales:view')")
    public Result<PageResult<SalesRankingReportDTO>> salesRankingReport(ReportQuery query) {
        return Result.success(reportService.getSalesRankingReport(query));
    }

    // ========== 采购报表 ==========

    @Log(title = "报表管理", action = "查询采购日报")
    @GetMapping("/purchase/daily")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:purchase:view')")
    public Result<PageResult<PurchaseDailyReportDTO>> purchaseDailyReport(ReportQuery query) {
        return Result.success(reportService.getPurchaseDailyReport(query));
    }

    @Log(title = "报表管理", action = "查询供应商分析")
    @GetMapping("/purchase/supplier-analysis")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:purchase:view')")
    public Result<PageResult<SupplierAnalysisDTO>> supplierAnalysisReport(ReportQuery query) {
        return Result.success(reportService.getSupplierAnalysisReport(query));
    }

    // ========== 库存报表 ==========

    @Log(title = "报表管理", action = "查询库存周转")
    @GetMapping("/inventory/turnover")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:inventory:view')")
    public Result<PageResult<InventoryTurnoverDTO>> inventoryTurnoverReport(ReportQuery query) {
        return Result.success(reportService.getInventoryTurnoverReport(query));
    }

    @Log(title = "报表管理", action = "查询库龄分析")
    @GetMapping("/inventory/age")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:inventory:view')")
    public Result<PageResult<InventoryAgeDTO>> inventoryAgeReport(ReportQuery query) {
        return Result.success(reportService.getInventoryAgeReport(query));
    }

    // ========== 生产报表 ==========

    @Log(title = "报表管理", action = "查询生产效率")
    @GetMapping("/production/efficiency")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:production:view')")
    public Result<PageResult<ProductionEfficiencyDTO>> productionEfficiencyReport(ReportQuery query) {
        return Result.success(reportService.getProductionEfficiencyReport(query));
    }

    @Log(title = "报表管理", action = "查询质量分析")
    @GetMapping("/production/quality")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:production:view')")
    public Result<PageResult<QualityAnalysisDTO>> qualityAnalysisReport(ReportQuery query) {
        return Result.success(reportService.getQualityAnalysisReport(query));
    }

    // ========== 财务报表 ==========

    @Log(title = "报表管理", action = "查询应收报表")
    @GetMapping("/finance/receivable")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:finance:view')")
    public Result<PageResult<ReceivableReportDTO>> receivableReport(ReportQuery query) {
        return Result.success(reportService.getReceivableReport(query));
    }

    @Log(title = "报表管理", action = "查询应付报表")
    @GetMapping("/finance/payable")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:finance:view')")
    public Result<PageResult<PayableReportDTO>> payableReport(ReportQuery query) {
        return Result.success(reportService.getPayableReport(query));
    }

    // ========== 工资报表 ==========

    @Log(title = "报表管理", action = "查询计件工资汇总")
    @GetMapping("/wage/piece-summary")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:wage:view')")
    public Result<PageResult<PieceWageSummaryDTO>> pieceWageSummaryReport(ReportQuery query) {
        return Result.success(reportService.getPieceWageSummaryReport(query));
    }
}
