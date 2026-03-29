package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.ProductCostDTO;
import com.duoduo.jxc.dto.cost.ProductCostQuery;
import com.duoduo.jxc.service.ProductCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/product")
@RequiredArgsConstructor
public class ProductCostController {

    private final ProductCostService productCostService;

    @Log(title = "产品成本", action = "分页查询产品成本")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:product:view')")
    public Result<PageResult<ProductCostDTO>> page(ProductCostQuery query) {
        return Result.success(productCostService.pageQuery(query));
    }

    @Log(title = "产品成本", action = "查询产品成本详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:product:view')")
    public Result<ProductCostDTO> getById(@PathVariable Long id) {
        return Result.success(productCostService.getDetail(id));
    }

    @Log(title = "产品成本", action = "创建产品成本")
    @PostMapping
    @PreAuthorize("@perm.has('cost:product:add')")
    public Result<Long> create(@RequestBody ProductCostDTO dto) {
        return Result.success(productCostService.create(dto));
    }

    @Log(title = "产品成本", action = "修改产品成本")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:product:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductCostDTO dto) {
        dto.setProductCostId(id);
        productCostService.update(dto);
        return Result.success();
    }

    @Log(title = "产品成本", action = "删除产品成本")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:product:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        productCostService.delete(id);
        return Result.success();
    }
}
