package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.fabric.FabricAlertDTO;
import com.duoduo.jxc.dto.fabric.FabricInventoryDTO;
import com.duoduo.jxc.dto.fabric.FabricInventoryQuery;
import com.duoduo.jxc.service.FabricInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fabrics/inventory")
@RequiredArgsConstructor
public class FabricInventoryController {

    private final FabricInventoryService fabricInventoryService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<PageResult<FabricInventoryDTO>> pageQuery(FabricInventoryQuery query) {
        return Result.success(fabricInventoryService.pageQuery(query));
    }

    @GetMapping("/{fabricId}")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<List<FabricInventoryDTO>> getByFabricId(@PathVariable Long fabricId) {
        return Result.success(fabricInventoryService.getByFabricId(fabricId));
    }

    @PostMapping("/lock")
    @PreAuthorize("@perm.has('fabric:edit')")
    public Result<Void> lockStock(@RequestParam Long warehouseId, @RequestParam Long fabricId, @RequestParam BigDecimal quantity) {
        fabricInventoryService.lockStock(warehouseId, fabricId, quantity);
        return Result.success();
    }

    @PostMapping("/unlock")
    @PreAuthorize("@perm.has('fabric:edit')")
    public Result<Void> unlockStock(@RequestParam Long warehouseId, @RequestParam Long fabricId, @RequestParam BigDecimal quantity) {
        fabricInventoryService.unlockStock(warehouseId, fabricId, quantity);
        return Result.success();
    }

    @GetMapping("/alerts")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<List<FabricAlertDTO>> checkAlerts() {
        return Result.success(fabricInventoryService.checkAlerts());
    }
}
