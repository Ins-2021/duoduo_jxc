package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.InventoryDTO;
import com.duoduo.jxc.dto.inventory.InventoryQuery;
import com.duoduo.jxc.entity.Inventory;
import com.duoduo.jxc.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Log(title = "库存管理", action = "分页查询库存")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('inventory:menu:view') or @perm.has('inventory:query:view')")
    public Result<PageResult<InventoryDTO>> page(InventoryQuery query) {
        return Result.success(inventoryService.pageQuery(query));
    }

    @Log(title = "库存管理", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:menu:view') or @perm.has('inventory:query:view')")
    public Result<Inventory> getById(@PathVariable Long id) {
        return Result.success(inventoryService.getById(id));
    }

    @Log(title = "库存管理", action = "删除库存记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        inventoryService.removeById(id);
        return Result.success();
    }

    @Log(title = "库存管理", action = "批量删除库存记录")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('inventory:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        inventoryService.removeByIds(ids);
        return Result.success();
    }
}
