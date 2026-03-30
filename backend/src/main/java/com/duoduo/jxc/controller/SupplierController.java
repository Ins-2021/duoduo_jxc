package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.data.SupplierDTO;
import com.duoduo.jxc.dto.data.SupplierQuery;
import com.duoduo.jxc.entity.Supplier;
import com.duoduo.jxc.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:supplier:view')")
    public Result<PageResult<Supplier>> pageQuery(SupplierQuery query) {
        return Result.success(supplierService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:supplier:view')")
    public Result<Supplier> getDetail(@PathVariable Long id) {
        return Result.success(supplierService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('data:supplier:add')")
    public Result<Long> create(@RequestBody SupplierDTO dto) {
        return Result.success(supplierService.create(dto));
    }

    @PutMapping
    @PreAuthorize("@perm.has('data:supplier:edit')")
    public Result<Void> update(@RequestBody SupplierDTO dto) {
        supplierService.update(dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('data:supplier:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody SupplierDTO dto) {
        supplierService.updateStatus(id, dto.getStatus());
        return Result.success();
    }
}
