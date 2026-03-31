package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.MaterialRequirementDTO;
import com.duoduo.jxc.dto.production.ProductionInboundDTO;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.service.ProductionOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production/order")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @Log(title = "生产订单", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('production:order:view')")
    public Result<PageResult<ProductionOrderDTO>> page(ProductionOrderQuery query) {
        return Result.success(productionOrderService.pageQuery(query));
    }

    @Log(title = "生产订单", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('production:order:view')")
    public Result<ProductionOrderDTO> getById(@PathVariable Long id) {
        return Result.success(productionOrderService.getDetail(id));
    }

    @Log(title = "生产订单", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('production:order:add')")
    public Result<Long> create(@RequestBody ProductionOrderDTO dto) {
        return Result.success(productionOrderService.create(dto));
    }

    @Log(title = "生产订单", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('production:order:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductionOrderDTO dto) {
        dto.setOrderId(id);
        productionOrderService.update(dto);
        return Result.success();
    }

    @Log(title = "生产订单", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('production:order:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        productionOrderService.delete(id);
        return Result.success();
    }

    @Log(title = "生产订单", action = "更新状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('production:order:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        productionOrderService.updateStatus(id, status);
        return Result.success();
    }

    @Log(title = "生产订单", action = "计算面料需求")
    @GetMapping("/{id}/material-requirement")
    @PreAuthorize("@perm.has('production:order:view')")
    public Result<MaterialRequirementDTO> calculateMaterialRequirement(@PathVariable Long id) {
        return Result.success(productionOrderService.calculateMaterialRequirement(id));
    }

    @Log(title = "生产订单", action = "生产入库")
    @PostMapping("/{id}/inbound")
    @PreAuthorize("@perm.has('production:order:inbound')")
    public Result<Void> productionInbound(@PathVariable Long id, @Valid @RequestBody ProductionInboundDTO dto) {
        dto.setProductionOrderId(id);
        productionOrderService.productionInbound(dto);
        return Result.success();
    }
}
