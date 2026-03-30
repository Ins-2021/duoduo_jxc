package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ColorDTO;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/color")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    @Log(title = "颜色管理", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:color:view')")
    public Result<PageResult<ColorDTO>> page(PageQuery query) {
        return Result.success(colorService.pageQuery(query));
    }

    @Log(title = "颜色管理", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:color:view')")
    public Result<ColorDTO> getById(@PathVariable Long id) {
        return Result.success(colorService.getDetail(id));
    }

    @Log(title = "颜色管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:color:add')")
    public Result<Long> add(@RequestBody @Validated ColorDTO dto) {
        return Result.success(colorService.create(dto));
    }

    @Log(title = "颜色管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:color:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated ColorDTO dto) {
        dto.setColorId(id);
        colorService.update(dto);
        return Result.success();
    }

    @Log(title = "颜色管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:color:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        colorService.delete(id);
        return Result.success();
    }

    @Log(title = "颜色管理", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:color:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        colorService.removeByIds(ids);
        return Result.success();
    }
}
