package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.StockInOutDTO;
import com.duoduo.jxc.service.StockInOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/stock-in-out")
@RequiredArgsConstructor
public class StockInOutController {

    private final StockInOutService stockInOutService;

    @Log(title = "出入库单管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('inventory:stock-in-out:view') or @perm.has('inventory:stock-in:view') or @perm.has('inventory:stock-out:view')")
    public Result<PageResult<StockInOutDTO>> page(@RequestBody PageQuery query) {
        return Result.success(stockInOutService.pageList(query));
    }

    @Log(title = "出入库单管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:stock-in-out:view') or @perm.has('inventory:stock-in:view') or @perm.has('inventory:stock-out:view')")
    public Result<StockInOutDTO> getById(@PathVariable Long id) {
        return Result.success(stockInOutService.getById(id));
    }

    @Log(title = "出入库单管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('inventory:stock-in-out:add')")
    public Result<Long> create(@RequestBody StockInOutDTO dto) {
        return Result.success(stockInOutService.create(dto));
    }

    @Log(title = "出入库单管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:stock-in-out:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody StockInOutDTO dto) {
        dto.setInOutId(id);
        stockInOutService.update(dto);
        return Result.success();
    }

    @Log(title = "出入库单管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:stock-in-out:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        stockInOutService.delete(id);
        return Result.success();
    }

    @Log(title = "出入库单管理", action = "审核")
    @PostMapping("/{id}/approve")
    @PreAuthorize("@perm.has('inventory:stock-in-out:approve')")
    public Result<Void> approve(@PathVariable Long id) {
        stockInOutService.approve(id);
        return Result.success();
    }
}
