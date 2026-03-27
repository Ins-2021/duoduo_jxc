package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.AssemblyOrderDTO;
import com.duoduo.jxc.service.AssemblyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/assembly")
@RequiredArgsConstructor
public class AssemblyOrderController {

    private final AssemblyOrderService assemblyOrderService;

    @Log(title = "组装拆卸单管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('inventory:assembly:view')")
    public Result<PageResult<AssemblyOrderDTO>> page(@RequestBody PageQuery query) {
        return Result.success(assemblyOrderService.pageList(query));
    }

    @Log(title = "组装拆卸单管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:assembly:view')")
    public Result<AssemblyOrderDTO> getById(@PathVariable Long id) {
        return Result.success(assemblyOrderService.getById(id));
    }

    @Log(title = "组装拆卸单管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('inventory:assembly:add')")
    public Result<Long> create(@RequestBody AssemblyOrderDTO dto) {
        return Result.success(assemblyOrderService.create(dto));
    }

    @Log(title = "组装拆卸单管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:assembly:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody AssemblyOrderDTO dto) {
        dto.setAssemblyId(id);
        assemblyOrderService.update(dto);
        return Result.success();
    }

    @Log(title = "组装拆卸单管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:assembly:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        assemblyOrderService.delete(id);
        return Result.success();
    }

    @Log(title = "组装拆卸单管理", action = "审核")
    @PostMapping("/{id}/approve")
    @PreAuthorize("@perm.has('inventory:assembly:approve')")
    public Result<Void> approve(@PathVariable Long id) {
        assemblyOrderService.approve(id);
        return Result.success();
    }
}
