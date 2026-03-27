package com.duoduo.jxc.controller.retail;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.retail.RetailSettlementDTO;
import com.duoduo.jxc.dto.retail.RetailSettlementQuery;
import com.duoduo.jxc.service.retail.RetailSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retail/settlement")
@RequiredArgsConstructor
public class RetailSettlementController {

    private final RetailSettlementService settlementService;

    /**
     * 生成日结单
     * POST /api/retail/settlement/daily
     */
    @PostMapping("/daily")
    @PreAuthorize("@perm.has('retail:settlement:create')")
    public Result<Long> createDailySettlement(@RequestBody RetailSettlementDTO dto) {
        return Result.success(settlementService.createDailySettlement(dto));
    }

    /**
     * 日结单列表
     * GET /api/retail/settlement/page
     */
    @GetMapping("/page")
    @PreAuthorize("@perm.has('retail:settlement:view')")
    public Result<PageResult<RetailSettlementDTO>> pageQuery(RetailSettlementQuery query) {
        return Result.success(settlementService.pageQuery(query));
    }
}