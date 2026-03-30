package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.report.*;
import com.duoduo.jxc.service.ProfitAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 利润分析控制器
 */
@RestController
@RequestMapping("/api/report/profit")
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
}
