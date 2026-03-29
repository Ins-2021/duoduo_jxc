package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.data.SupplierReconciliationDTO;
import com.duoduo.jxc.dto.data.SupplierReconciliationQuery;
import com.duoduo.jxc.service.data.SupplierReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplier/reconciliation")
@RequiredArgsConstructor
public class SupplierReconciliationController {

    private final SupplierReconciliationService reconciliationService;

    @Log(title = "供应商对账", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:supplier:view')")
    public Result<PageResult<SupplierReconciliationDTO>> page(SupplierReconciliationQuery query) {
        return Result.success(reconciliationService.pageQuery(query));
    }

    @Log(title = "供应商对账", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:supplier:view')")
    public Result<SupplierReconciliationDTO> getDetail(@PathVariable Long id) {
        return Result.success(reconciliationService.getDetail(id));
    }

    @Log(title = "供应商对账", action = "新增对账单")
    @PostMapping
    @PreAuthorize("@perm.has('data:supplier:add')")
    public Result<Long> create(@RequestBody SupplierReconciliationDTO dto) {
        return Result.success(reconciliationService.create(dto));
    }

    @Log(title = "供应商对账", action = "修改对账单")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:supplier:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SupplierReconciliationDTO dto) {
        dto.setReconciliationId(id);
        reconciliationService.update(dto);
        return Result.success();
    }

    @Log(title = "供应商对账", action = "确认对账")
    @PostMapping("/{id}/confirm")
    @PreAuthorize("@perm.has('data:supplier:edit')")
    public Result<Void> confirm(@PathVariable Long id) {
        reconciliationService.confirm(id);
        return Result.success();
    }
}
