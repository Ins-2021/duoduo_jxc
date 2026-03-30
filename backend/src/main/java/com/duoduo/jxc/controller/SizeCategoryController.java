package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.SizeCategoryDTO;
import com.duoduo.jxc.service.SizeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizecategory")
@RequiredArgsConstructor
public class SizeCategoryController {

    private final SizeCategoryService sizeCategoryService;

    @Log(title = "尺码分类", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizecategory:view')")
    public Result<PageResult<SizeCategoryDTO>> page(PageQuery query) {
        return Result.success(sizeCategoryService.pageQuery(query));
    }

    @Log(title = "尺码分类", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizecategory:view')")
    public Result<SizeCategoryDTO> getById(@PathVariable Long id) {
        return Result.success(sizeCategoryService.getDetail(id));
    }

    @Log(title = "尺码分类", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:sizecategory:add')")
    public Result<Long> add(@RequestBody SizeCategoryDTO dto) {
        return Result.success(sizeCategoryService.create(dto));
    }

    @Log(title = "尺码分类", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizecategory:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SizeCategoryDTO dto) {
        dto.setCategoryId(id);
        sizeCategoryService.update(dto);
        return Result.success();
    }

    @Log(title = "尺码分类", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizecategory:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sizeCategoryService.delete(id);
        return Result.success();
    }

    @Log(title = "尺码分类", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:sizecategory:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        sizeCategoryService.removeByIds(ids);
        return Result.success();
    }
}
