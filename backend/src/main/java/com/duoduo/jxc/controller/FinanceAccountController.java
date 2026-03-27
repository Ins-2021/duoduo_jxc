package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.FinanceAccountDTO;
import com.duoduo.jxc.dto.finance.FinanceAccountQuery;
import com.duoduo.jxc.service.FinanceAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finance/account")
@RequiredArgsConstructor
public class FinanceAccountController {

    private final FinanceAccountService financeAccountService;

    @Log(title = "财务管理", action = "分页查询资金账户")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('finance:account:view')")
    public Result<PageResult<FinanceAccountDTO>> page(FinanceAccountQuery query) {
        return Result.success(financeAccountService.pageQuery(query));
    }

    @Log(title = "财务管理", action = "新增资金账户")
    @PostMapping
    @PreAuthorize("@perm.has('finance:account:add')")
    public Result<Long> add(@RequestBody FinanceAccountDTO dto) {
        return Result.success(financeAccountService.addAccount(dto));
    }
}
