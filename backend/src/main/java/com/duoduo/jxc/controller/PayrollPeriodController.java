package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.wage.PayrollPeriodDTO;
import com.duoduo.jxc.dto.wage.PayrollPeriodQuery;
import com.duoduo.jxc.service.PayrollPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wage/period")
@RequiredArgsConstructor
public class PayrollPeriodController {

    private final PayrollPeriodService payrollPeriodService;

    @Log(title = "工资单", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('wage:sheet:view')")
    public Result<PageResult<PayrollPeriodDTO>> page(PayrollPeriodQuery query) {
        return Result.success(payrollPeriodService.pageQuery(query));
    }

    @Log(title = "工资单", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('wage:sheet:view')")
    public Result<PayrollPeriodDTO> getById(@PathVariable Long id) {
        return Result.success(payrollPeriodService.getDetail(id));
    }

    @Log(title = "工资单", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('wage:sheet:add')")
    public Result<Long> create(@RequestBody PayrollPeriodDTO dto) {
        return Result.success(payrollPeriodService.create(dto));
    }

    @Log(title = "工资单", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('wage:sheet:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody PayrollPeriodDTO dto) {
        dto.setId(id);
        payrollPeriodService.update(dto);
        return Result.success();
    }

    @Log(title = "工资单", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('wage:sheet:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        payrollPeriodService.delete(id);
        return Result.success();
    }

    @Log(title = "工资单", action = "更新状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('wage:sheet:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        payrollPeriodService.updateStatus(id, status);
        return Result.success();
    }

    @Log(title = "工资单", action = "提交审核")
    @PostMapping("/{id}/submit")
    @PreAuthorize("@perm.has('wage:sheet:edit')")
    public Result<Void> submit(@PathVariable Long id) {
        payrollPeriodService.submit(id);
        return Result.success();
    }

    @Log(title = "工资单", action = "审核")
    @PostMapping("/{id}/audit")
    @PreAuthorize("@perm.has('wage:sheet:audit')")
    public Result<Void> audit(@PathVariable Long id, @RequestParam boolean approved) {
        payrollPeriodService.audit(id, approved);
        return Result.success();
    }
}
