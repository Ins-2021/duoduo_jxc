package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.ProductionPlanDTO;
import com.duoduo.jxc.dto.production.ProductionPlanQuery;
import com.duoduo.jxc.service.ProductionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production/plan")
@RequiredArgsConstructor
public class ProductionPlanController {

    private final ProductionPlanService productionPlanService;

    @Log(title = "生产计划", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('production:plan:view')")
    public Result<PageResult<ProductionPlanDTO>> page(ProductionPlanQuery query) {
        return Result.success(productionPlanService.pageQuery(query));
    }

    @Log(title = "生产计划", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('production:plan:view')")
    public Result<ProductionPlanDTO> getById(@PathVariable Long id) {
        return Result.success(productionPlanService.getDetail(id));
    }

    @Log(title = "生产计划", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('production:plan:add')")
    public Result<Long> create(@RequestBody ProductionPlanDTO dto) {
        return Result.success(productionPlanService.create(dto));
    }

    @Log(title = "生产计划", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('production:plan:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductionPlanDTO dto) {
        dto.setPlanId(id);
        productionPlanService.update(dto);
        return Result.success();
    }

    @Log(title = "生产计划", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('production:plan:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        productionPlanService.delete(id);
        return Result.success();
    }

    @Log(title = "生产计划", action = "更新状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('production:plan:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        productionPlanService.updateStatus(id, status);
        return Result.success();
    }
}
