package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.FinanceTransactionDTO;
import com.duoduo.jxc.service.FinanceTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 财务流水Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/finance/transaction")
@RequiredArgsConstructor
public class FinanceTransactionController {

    private final FinanceTransactionService financeTransactionService;

    @Log(title = "财务流水", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('finance:transaction:view')")
    public Result<PageResult<FinanceTransactionDTO>> page(@RequestBody PageQuery query) {
        return Result.success(financeTransactionService.pageList(query));
    }

    @Log(title = "财务流水", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:transaction:view')")
    public Result<FinanceTransactionDTO> getById(@PathVariable Long id) {
        return Result.success(financeTransactionService.getById(id));
    }
}
