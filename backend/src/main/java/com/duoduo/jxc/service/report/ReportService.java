package com.duoduo.jxc.service.report;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.report.*;

import java.util.List;

public interface ReportService {
    
    // ========== 销售报表 ==========
    PageResult<SalesDailyReportDTO> getSalesDailyReport(SalesReportQuery query);
    List<SalesDailyReportDTO> exportSalesDailyReport(SalesReportQuery query);
    
    PageResult<SalesMonthlyReportDTO> getSalesMonthlyReport(ReportQuery query);
    
    PageResult<SalesRankingReportDTO> getSalesRankingReport(ReportQuery query);
    
    // ========== 采购报表 ==========
    PageResult<PurchaseDailyReportDTO> getPurchaseDailyReport(ReportQuery query);
    
    PageResult<SupplierAnalysisDTO> getSupplierAnalysisReport(ReportQuery query);
    
    // ========== 库存报表 ==========
    PageResult<InventoryTurnoverDTO> getInventoryTurnoverReport(ReportQuery query);
    
    PageResult<InventoryAgeDTO> getInventoryAgeReport(ReportQuery query);
    
    // ========== 生产报表 ==========
    PageResult<ProductionEfficiencyDTO> getProductionEfficiencyReport(ReportQuery query);
    
    PageResult<QualityAnalysisDTO> getQualityAnalysisReport(ReportQuery query);
    
    // ========== 财务报表 ==========
    PageResult<ReceivableReportDTO> getReceivableReport(ReportQuery query);
    
    PageResult<PayableReportDTO> getPayableReport(ReportQuery query);
    
    // ========== 工资报表 ==========
    PageResult<PieceWageSummaryDTO> getPieceWageSummaryReport(ReportQuery query);
}
