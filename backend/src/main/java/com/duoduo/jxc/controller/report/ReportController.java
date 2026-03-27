package com.duoduo.jxc.controller.report;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.report.SalesDailyReportDTO;
import com.duoduo.jxc.dto.report.SalesReportQuery;
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

    @Log(title = "报表管理", action = "查询销售日报")
    @GetMapping("/sales-daily")
    @PreAuthorize("@perm.has('reports:menu:view') or @perm.has('report:sales:view')")
    public Result<PageResult<SalesDailyReportDTO>> salesDailyReport(SalesReportQuery query) {
        return Result.success(reportService.getSalesDailyReport(query));
    }
}
