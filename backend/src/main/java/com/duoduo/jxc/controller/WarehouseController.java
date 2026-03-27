package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.data.WarehouseDTO;
import com.duoduo.jxc.dto.data.WarehouseQuery;
import com.duoduo.jxc.entity.Warehouse;
import com.duoduo.jxc.service.data.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:warehouse:view')")
    public Result<PageResult<Warehouse>> pageQuery(WarehouseQuery query) {
        return Result.success(warehouseService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:warehouse:view')")
    public Result<Warehouse> getDetail(@PathVariable Long id) {
        return Result.success(warehouseService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('data:warehouse:add')")
    public Result<Long> create(@RequestBody WarehouseDTO dto) {
        return Result.success(warehouseService.create(dto));
    }

    @PutMapping
    @PreAuthorize("@perm.has('data:warehouse:edit')")
    public Result<Void> update(@RequestBody WarehouseDTO dto) {
        warehouseService.update(dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('data:warehouse:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        warehouseService.updateStatus(id, dto.getStatus());
        return Result.success();
    }
}
