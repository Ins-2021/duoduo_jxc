package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.InventoryTransactionDTO;
import com.duoduo.jxc.dto.inventory.InventoryTransactionQuery;
import com.duoduo.jxc.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/transaction")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    @Log(title = "库存流水", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('inventory:transaction:view')")
    public Result<PageResult<InventoryTransactionDTO>> page(InventoryTransactionQuery query) {
        return Result.success(transactionService.pageQuery(query));
    }

    @Log(title = "库存流水", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:transaction:view')")
    public Result<InventoryTransactionDTO> getById(@PathVariable Long id) {
        return Result.success(transactionService.getDetail(id));
    }
}
