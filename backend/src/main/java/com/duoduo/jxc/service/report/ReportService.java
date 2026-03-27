package com.duoduo.jxc.service.report;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.report.SalesDailyReportDTO;
import com.duoduo.jxc.dto.report.SalesReportQuery;

import java.util.List;

public interface ReportService {
    PageResult<SalesDailyReportDTO> getSalesDailyReport(SalesReportQuery query);
    List<SalesDailyReportDTO> exportSalesDailyReport(SalesReportQuery query);
}
