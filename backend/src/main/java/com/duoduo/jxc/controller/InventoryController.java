package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.InventoryDTO;
import com.duoduo.jxc.dto.inventory.InventoryQuery;
import com.duoduo.jxc.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
