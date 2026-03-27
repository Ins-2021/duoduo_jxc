package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.InventoryAlertDTO;
import com.duoduo.jxc.service.InventoryAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/alert")
@RequiredArgsConstructor
public class InventoryAlertController {

    private final InventoryAlertService inventoryAlertService;

    @Log(title = "库存预警管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('inventory:alert:view')")
    public Result<PageResult<InventoryAlertDTO>> page(@RequestBody PageQuery query) {
        return Result.success(inventoryAlertService.pageList(query));
    }

    @Log(title = "库存预警管理", action = "标记为已处理")
    @PostMapping("/{id}/handle")
    @PreAuthorize("@perm.has('inventory:alert:handle')")
    public Result<Void> markAsHandled(@PathVariable Long id) {
        inventoryAlertService.markAsHandled(id);
        return Result.success();
    }

    @Log(title = "库存预警管理", action = "检查预警")
    @PostMapping("/check")
    @PreAuthorize("@perm.has('inventory:alert:check')")
    public Result<Void> checkAlerts() {
        inventoryAlertService.checkAlerts();
        return Result.success();
    }
}
