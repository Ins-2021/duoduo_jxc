package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.InventoryCheckDTO;
import com.duoduo.jxc.service.InventoryCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/check")
@RequiredArgsConstructor
public class InventoryCheckController {

    private final InventoryCheckService inventoryCheckService;

    @Log(title = "盘点单管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('inventory:check:view')")
    public Result<PageResult<InventoryCheckDTO>> page(@RequestBody PageQuery query) {
        return Result.success(inventoryCheckService.pageList(query));
    }

    @GetMapping("/page")
    @PreAuthorize("@perm.has('inventory:check:view')")
    public Result<PageResult<InventoryCheckDTO>> pageGet(PageQuery query) {
        return Result.success(inventoryCheckService.pageList(query));
    }

    @Log(title = "盘点单管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:check:view')")
    public Result<InventoryCheckDTO> getById(@PathVariable Long id) {
        return Result.success(inventoryCheckService.getById(id));
    }

    @Log(title = "盘点单管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('inventory:check:add')")
    public Result<Long> create(@RequestBody InventoryCheckDTO dto) {
        return Result.success(inventoryCheckService.create(dto));
    }

    @Log(title = "盘点单管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:check:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody InventoryCheckDTO dto) {
        dto.setCheckId(id);
        inventoryCheckService.update(dto);
        return Result.success();
    }

    @Log(title = "盘点单管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:check:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        inventoryCheckService.delete(id);
        return Result.success();
    }

    @Log(title = "盘点单管理", action = "完成盘点")
    @PostMapping("/{id}/complete")
    @PreAuthorize("@perm.has('inventory:check:complete')")
    public Result<Void> complete(@PathVariable Long id) {
        inventoryCheckService.complete(id);
        return Result.success();
    }
}
