package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.IncomeExpenseDTO;
import com.duoduo.jxc.service.IncomeExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 收支单Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/finance/income-expense")
@RequiredArgsConstructor
public class IncomeExpenseController {

    private final IncomeExpenseService incomeExpenseService;

    @Log(title = "收支管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('finance:income-expense:view')")
    public Result<PageResult<IncomeExpenseDTO>> page(@RequestBody PageQuery query) {
        return Result.success(incomeExpenseService.pageList(query));
    }

    @Log(title = "收支管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:income-expense:view')")
    public Result<IncomeExpenseDTO> getById(@PathVariable Long id) {
        return Result.success(incomeExpenseService.getById(id));
    }

    @Log(title = "收支管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('finance:income-expense:add')")
    public Result<Long> create(@RequestBody IncomeExpenseDTO dto) {
        return Result.success(incomeExpenseService.create(dto));
    }

    @Log(title = "收支管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('finance:income-expense:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody IncomeExpenseDTO dto) {
        dto.setIeId(id);
        incomeExpenseService.update(dto);
        return Result.success();
    }

    @Log(title = "收支管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('finance:income-expense:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        incomeExpenseService.delete(id);
        return Result.success();
    }

    @Log(title = "收支管理", action = "审核")
    @PostMapping("/{id}/approve")
    @PreAuthorize("@perm.has('finance:income-expense:approve')")
    public Result<Void> approve(@PathVariable Long id) {
        incomeExpenseService.approve(id);
        return Result.success();
    }
}
