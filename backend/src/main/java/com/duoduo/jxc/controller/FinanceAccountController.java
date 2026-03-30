package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.FinanceAccountDTO;
import com.duoduo.jxc.dto.finance.FinanceAccountQuery;
import com.duoduo.jxc.entity.FinanceAccount;
import com.duoduo.jxc.service.FinanceAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finance/account")
@RequiredArgsConstructor
public class FinanceAccountController {

    private final FinanceAccountService financeAccountService;

    @Log(title = "财务账户", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('finance:account:view')")
    public Result<PageResult<FinanceAccountDTO>> page(FinanceAccountQuery query) {
        return Result.success(financeAccountService.pageQuery(query));
    }

    @Log(title = "财务账户", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:account:view')")
    public Result<FinanceAccount> getById(@PathVariable Long id) {
        return Result.success(financeAccountService.getById(id));
    }

    @Log(title = "财务账户", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('finance:account:add')")
    public Result<Long> add(@RequestBody FinanceAccountDTO dto) {
        return Result.success(financeAccountService.addAccount(dto));
    }

    @Log(title = "财务账户", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('finance:account:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FinanceAccountDTO dto) {
        dto.setAccountId(id);
        financeAccountService.updateById(dto);
        return Result.success();
    }

    @Log(title = "财务账户", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('finance:account:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        financeAccountService.removeById(id);
        return Result.success();
    }

    @Log(title = "财务账户", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('finance:account:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        financeAccountService.removeByIds(ids);
        return Result.success();
    }
}
