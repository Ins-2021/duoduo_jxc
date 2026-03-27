package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.dto.sales.SalesOrderQuery;
import com.duoduo.jxc.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @Log(title = "销售管理", action = "分页查询销售单")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('sales:order:view')")
    public Result<PageResult<SalesOrderDTO>> page(SalesOrderQuery query) {
        return Result.success(salesOrderService.pageQuery(query));
    }

    @Log(title = "销售管理", action = "查询销售单详情")
    @GetMapping("/detail/page")
    @PreAuthorize("@perm.has('sales:order:view')")
    public Result<PageResult<com.duoduo.jxc.dto.sales.SalesOrderDetailDTO>> detailPageQuery(com.duoduo.jxc.dto.sales.SalesOrderDetailQuery query) {
        return Result.success(salesOrderService.detailPageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('sales:order:view')")
    public Result<SalesOrderDTO> getById(@PathVariable Long id) {
        return Result.success(salesOrderService.getDetail(id));
    }

    @Log(title = "销售管理", action = "创建销售单")
    @PostMapping
    @PreAuthorize("@perm.has('sales:order:add')")
    public Result<Long> create(@RequestBody SalesOrderDTO dto) {
        return Result.success(salesOrderService.createOrder(dto));
    }

    @Log(title = "销售管理", action = "修改销售单")
    @PutMapping
    @PreAuthorize("@perm.has('sales:order:edit')")
    public Result<Void> update(@RequestBody SalesOrderDTO dto) {
        salesOrderService.updateOrder(dto);
        return Result.success();
    }

    @Log(title = "销售管理", action = "删除销售单")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('sales:order:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        salesOrderService.deleteOrder(id);
        return Result.success();
    }

    @Log(title = "销售管理", action = "审核销售单")
    @PostMapping("/{id}/audit")
    @PreAuthorize("@perm.has('sales:order:audit')")
    public Result<Void> audit(@PathVariable Long id) {
        salesOrderService.auditOrder(id);
        return Result.success();
    }

    @Log(title = "销售管理", action = "反审核销售单")
    @PostMapping("/{id}/unaudit")
    @PreAuthorize("@perm.has('sales:order:audit')")
    public Result<Void> unaudit(@PathVariable Long id) {
        salesOrderService.unauditOrder(id);
        return Result.success();
    }

    @Log(title = "销售管理", action = "预定转销售")
    @PostMapping("/{id}/convert")
    @PreAuthorize("@perm.has('sales:order:convert')")
    public Result<Void> convertToSales(@PathVariable Long id) {
        salesOrderService.convertToSales(id);
        return Result.success();
    }
}

