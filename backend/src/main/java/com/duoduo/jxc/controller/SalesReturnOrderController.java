package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnOrderDTO;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnOrderQuery;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnSourceOrderDTO;
import com.duoduo.jxc.service.sales.SalesReturnOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales-return")
@RequiredArgsConstructor
public class SalesReturnOrderController {

    private final SalesReturnOrderService salesReturnOrderService;

    @Log(title = "销售退货", action = "查询列表")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('sales:return:view')")
    public Result<PageResult<SalesReturnOrderDTO>> pageQuery(SalesReturnOrderQuery query) {
        return Result.success(salesReturnOrderService.pageQuery(query));
    }

    @Log(title = "销售退货", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('sales:return:view')")
    public Result<SalesReturnOrderDTO> getDetail(@PathVariable Long id) {
        return Result.success(salesReturnOrderService.getDetail(id));
    }

    @Log(title = "销售退货", action = "查询原销售单")
    @GetMapping("/source/{id}")
    @PreAuthorize("@perm.has('sales:return:add')")
    public Result<SalesReturnSourceOrderDTO> getSourceOrder(@PathVariable Long id) {
        return Result.success(salesReturnOrderService.getSourceOrder(id));
    }

    @Log(title = "销售退货", action = "新增退货单")
    @PostMapping
    @PreAuthorize("@perm.has('sales:return:add')")
    public Result<Long> createOrder(@RequestBody SalesReturnOrderDTO dto) {
        return Result.success(salesReturnOrderService.createOrder(dto));
    }

    @Log(title = "销售退货", action = "编辑退货单")
    @PutMapping
    @PreAuthorize("@perm.has('sales:return:edit')")
    public Result<Void> updateOrder(@RequestBody SalesReturnOrderDTO dto) {
        salesReturnOrderService.updateOrder(dto);
        return Result.success();
    }

    @Log(title = "销售退货", action = "删除退货单")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('sales:return:delete')")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        salesReturnOrderService.deleteOrder(id);
        return Result.success();
    }

    @Log(title = "销售退货", action = "审核退货单")
    @PostMapping("/{id}/audit")
    @PreAuthorize("@perm.has('sales:return:audit')")
    public Result<Void> auditOrder(@PathVariable Long id) {
        salesReturnOrderService.auditOrder(id);
        return Result.success();
    }
}
