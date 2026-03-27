package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderQuery;
import com.duoduo.jxc.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Log(title = "进货管理", action = "分页查询进货单")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('purchase:order:view')")
    public Result<PageResult<PurchaseOrderDTO>> page(PurchaseOrderQuery query) {
        return Result.success(purchaseOrderService.pageQuery(query));
    }

    @Log(title = "进货管理", action = "查询进货单详情")
    @GetMapping("/detail/page")
    @PreAuthorize("@perm.has('purchase:order:view')")
    public Result<PageResult<com.duoduo.jxc.dto.purchase.PurchaseOrderDetailDTO>> detailPageQuery(com.duoduo.jxc.dto.purchase.PurchaseOrderDetailQuery query) {
        return Result.success(purchaseOrderService.detailPageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('purchase:order:view')")
    public Result<PurchaseOrderDTO> getById(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getDetail(id));
    }

    @Log(title = "进货管理", action = "创建进货单")
    @PostMapping
    @PreAuthorize("@perm.has('purchase:order:add')")
    public Result<Long> create(@RequestBody PurchaseOrderDTO dto) {
        return Result.success(purchaseOrderService.createOrder(dto));
    }

    @Log(title = "进货管理", action = "修改进货单")
    @PutMapping
    @PreAuthorize("@perm.has('purchase:order:edit')")
    public Result<Void> update(@RequestBody PurchaseOrderDTO dto) {
        purchaseOrderService.updateOrder(dto);
        return Result.success();
    }

    @Log(title = "进货管理", action = "删除进货单")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('purchase:order:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        purchaseOrderService.deleteOrder(id);
        return Result.success();
    }

    @Log(title = "进货管理", action = "审核进货单")
    @PostMapping("/{id}/audit")
    @PreAuthorize("@perm.has('purchase:order:audit')")
    public Result<Void> audit(@PathVariable Long id) {
        purchaseOrderService.auditOrder(id);
        return Result.success();
    }
}
