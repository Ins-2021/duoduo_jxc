package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.BatchDTO;
import com.duoduo.jxc.dto.inventory.BatchQuery;
import com.duoduo.jxc.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @Log(title = "批次管理", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('inventory:batch:view')")
    public Result<PageResult<BatchDTO>> page(BatchQuery query) {
        return Result.success(batchService.pageQuery(query));
    }

    @Log(title = "批次管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:batch:view')")
    public Result<BatchDTO> getById(@PathVariable Long id) {
        return Result.success(batchService.getDetail(id));
    }

    @Log(title = "批次管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('inventory:batch:add')")
    public Result<Long> create(@RequestBody BatchDTO dto) {
        return Result.success(batchService.create(dto));
    }

    @Log(title = "批次管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:batch:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody BatchDTO dto) {
        dto.setBatchId(id);
        batchService.update(dto);
        return Result.success();
    }

    @Log(title = "批次管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:batch:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        batchService.delete(id);
        return Result.success();
    }
}
