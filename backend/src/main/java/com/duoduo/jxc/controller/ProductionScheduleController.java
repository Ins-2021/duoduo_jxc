package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.ProductionScheduleDTO;
import com.duoduo.jxc.dto.production.ProductionScheduleQuery;
import com.duoduo.jxc.service.ProductionScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production/schedule")
@RequiredArgsConstructor
public class ProductionScheduleController {

    private final ProductionScheduleService productionScheduleService;

    @Log(title = "生产排程", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('production:schedule:view')")
    public Result<PageResult<ProductionScheduleDTO>> page(ProductionScheduleQuery query) {
        return Result.success(productionScheduleService.pageQuery(query));
    }

    @Log(title = "生产排程", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('production:schedule:view')")
    public Result<ProductionScheduleDTO> getById(@PathVariable Long id) {
        return Result.success(productionScheduleService.getDetail(id));
    }

    @Log(title = "生产排程", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('production:schedule:add')")
    public Result<Long> create(@RequestBody ProductionScheduleDTO dto) {
        return Result.success(productionScheduleService.create(dto));
    }

    @Log(title = "生产排程", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('production:schedule:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductionScheduleDTO dto) {
        dto.setScheduleId(id);
        productionScheduleService.update(dto);
        return Result.success();
    }

    @Log(title = "生产排程", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('production:schedule:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        productionScheduleService.delete(id);
        return Result.success();
    }

    @Log(title = "生产排程", action = "更新状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('production:schedule:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        productionScheduleService.updateStatus(id, status);
        return Result.success();
    }
}
