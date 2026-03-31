package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.TransferOrderDTO;
import com.duoduo.jxc.service.TransferOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/transfer")
@RequiredArgsConstructor
public class TransferOrderController {

    private final TransferOrderService transferOrderService;

    @Log(title = "调拨单管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('inventory:transfer:view')")
    public Result<PageResult<TransferOrderDTO>> page(@RequestBody PageQuery query) {
        return Result.success(transferOrderService.pageList(query));
    }

    @GetMapping("/page")
    @PreAuthorize("@perm.has('inventory:transfer:view')")
    public Result<PageResult<TransferOrderDTO>> pageGet(PageQuery query) {
        return Result.success(transferOrderService.pageList(query));
    }

    @Log(title = "调拨单管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:transfer:view')")
    public Result<TransferOrderDTO> getById(@PathVariable Long id) {
        return Result.success(transferOrderService.getById(id));
    }

    @Log(title = "调拨单管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('inventory:transfer:add')")
    public Result<Long> create(@RequestBody TransferOrderDTO dto) {
        return Result.success(transferOrderService.create(dto));
    }

    @Log(title = "调拨单管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:transfer:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody TransferOrderDTO dto) {
        dto.setTransferId(id);
        transferOrderService.update(dto);
        return Result.success();
    }

    @Log(title = "调拨单管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:transfer:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        transferOrderService.delete(id);
        return Result.success();
    }

    @Log(title = "调拨单管理", action = "审核")
    @PostMapping("/{id}/approve")
    @PreAuthorize("@perm.has('inventory:transfer:approve')")
    public Result<Void> approve(@PathVariable Long id) {
        transferOrderService.approve(id);
        return Result.success();
    }

    @Log(title = "调拨单管理", action = "驳回")
    @PostMapping("/{id}/reject")
    @PreAuthorize("@perm.has('inventory:transfer:reject')")
    public Result<Void> reject(@PathVariable Long id) {
        transferOrderService.reject(id);
        return Result.success();
    }
}
