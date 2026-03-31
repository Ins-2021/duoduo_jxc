package com.duoduo.jxc.controller;

import com.alibaba.excel.EasyExcel;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.report.*;
import com.duoduo.jxc.service.ProfitAnalysisService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 利润分析控制器
 */
@RestController
@RequestMapping("/report/profit")
@RequiredArgsConstructor
public class ProfitAnalysisController {

    private final ProfitAnalysisService profitAnalysisService;

    @GetMapping("/summary")
    public Result<ProfitSummaryDTO> getProfitSummary(ProfitQueryDTO query) {
        return Result.success(profitAnalysisService.getProfitSummary(query));
    }

    @GetMapping("/style-ranking")
    public Result<List<StyleProfitDTO>> getStyleProfitRanking(ProfitQueryDTO query) {
        return Result.success(profitAnalysisService.getStyleProfitRanking(query));
    }

    @GetMapping("/customer-ranking")
    public Result<List<CustomerProfitDTO>> getCustomerProfitRanking(ProfitQueryDTO query) {
        return Result.success(profitAnalysisService.getCustomerProfitRanking(query));
    }

    @GetMapping("/trend")
    public Result<ProfitTrendDTO> getProfitTrend(ProfitQueryDTO query) {
        return Result.success(profitAnalysisService.getProfitTrend(query));
    }

    @GetMapping("/style-detail/{styleId}")
    public Result<StyleProfitDetailDTO> getStyleProfitDetail(
            @PathVariable Long styleId,
            ProfitQueryDTO query) {
        return Result.success(profitAnalysisService.getStyleProfitDetail(styleId, query));
    }

    @GetMapping("/export/style-ranking")
    public void exportStyleProfitRanking(ProfitQueryDTO query, HttpServletResponse response) throws IOException {
        List<StyleProfitDTO> data = profitAnalysisService.getStyleProfitRanking(query);
        exportExcel(data, "款式利润排行", StyleProfitDTO.class, response);
    }

    @GetMapping("/export/customer-ranking")
    public void exportCustomerProfitRanking(ProfitQueryDTO query, HttpServletResponse response) throws IOException {
        List<CustomerProfitDTO> data = profitAnalysisService.getCustomerProfitRanking(query);
        exportExcel(data, "客户利润排行", CustomerProfitDTO.class, response);
    }

    @GetMapping("/export/trend")
    public void exportProfitTrend(ProfitQueryDTO query, HttpServletResponse response) throws IOException {
        ProfitTrendDTO data = profitAnalysisService.getProfitTrend(query);
        List<MonthlyProfitDTO> monthlyProfits = new java.util.ArrayList<>();
        if (data.getDates() != null) {
            for (int i = 0; i < data.getDates().size(); i++) {
                MonthlyProfitDTO dto = new MonthlyProfitDTO();
                dto.setMonth(data.getDates().get(i));
                if (data.getSalesAmount() != null && i < data.getSalesAmount().size()) {
                    dto.setSalesAmount(data.getSalesAmount().get(i));
                }
                if (data.getCostAmount() != null && i < data.getCostAmount().size()) {
                    dto.setCostAmount(data.getCostAmount().get(i));
                }
                if (data.getGrossProfit() != null && i < data.getGrossProfit().size()) {
                    dto.setGrossProfit(data.getGrossProfit().get(i));
                }
                monthlyProfits.add(dto);
            }
        }
        exportExcel(monthlyProfits, "利润趋势", MonthlyProfitDTO.class, response);
    }

    private <T> void exportExcel(List<T> data, String fileName, Class<T> clazz, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName + "_" + System.currentTimeMillis(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz).sheet(fileName).doWrite(data);
    }
}
