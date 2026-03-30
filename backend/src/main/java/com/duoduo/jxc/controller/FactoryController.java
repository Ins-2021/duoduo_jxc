package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.FactoryDTO;
import com.duoduo.jxc.service.FactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factory")
@RequiredArgsConstructor
public class FactoryController {

    private final FactoryService factoryService;

    @Log(title = "工厂管理", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:factory:view')")
    public Result<PageResult<FactoryDTO>> page(PageQuery query) {
        return Result.success(factoryService.pageQuery(query));
    }

    @Log(title = "工厂管理", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:factory:view')")
    public Result<FactoryDTO> getById(@PathVariable Long id) {
        return Result.success(factoryService.getDetail(id));
    }

    @Log(title = "工厂管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:factory:add')")
    public Result<Long> add(@RequestBody FactoryDTO dto) {
        return Result.success(factoryService.create(dto));
    }

    @Log(title = "工厂管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:factory:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FactoryDTO dto) {
        dto.setFactoryId(id);
        factoryService.update(dto);
        return Result.success();
    }

    @Log(title = "工厂管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:factory:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        factoryService.delete(id);
        return Result.success();
    }

    @Log(title = "工厂管理", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:factory:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        factoryService.removeByIds(ids);
        return Result.success();
    }
}
