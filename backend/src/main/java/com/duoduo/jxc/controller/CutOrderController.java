package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.CutOrderDTO;
import com.duoduo.jxc.dto.production.CutOrderQuery;
import com.duoduo.jxc.service.CutOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production/cut-order")
@RequiredArgsConstructor
public class CutOrderController {

    private final CutOrderService cutOrderService;

    @Log(title = "裁床单", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('production:cut-order:view')")
    public Result<PageResult<CutOrderDTO>> page(CutOrderQuery query) {
        return Result.success(cutOrderService.pageQuery(query));
    }

    @Log(title = "裁床单", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('production:cut-order:view')")
    public Result<CutOrderDTO> getById(@PathVariable Long id) {
        return Result.success(cutOrderService.getDetail(id));
    }

    @Log(title = "裁床单", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('production:cut-order:add')")
    public Result<Long> create(@RequestBody CutOrderDTO dto) {
        return Result.success(cutOrderService.create(dto));
    }

    @Log(title = "裁床单", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('production:cut-order:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CutOrderDTO dto) {
        dto.setPlanId(id);
        cutOrderService.update(dto);
        return Result.success();
    }

    @Log(title = "裁床单", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('production:cut-order:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        cutOrderService.delete(id);
        return Result.success();
    }

    @Log(title = "裁床单", action = "更新状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('production:cut-order:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        cutOrderService.updateStatus(id, status);
        return Result.success();
    }

    @Log(title = "裁床单", action = "完成裁床")
    @PutMapping("/{id}/complete")
    @PreAuthorize("@perm.has('production:cut-order:edit')")
    public Result<Void> completeCutting(@PathVariable Long id, @RequestParam Integer cutQuantity) {
        cutOrderService.completeCutting(id, cutQuantity);
        return Result.success();
    }
}
