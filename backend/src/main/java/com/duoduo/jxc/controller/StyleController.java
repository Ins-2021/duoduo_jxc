package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.StyleDTO;
import com.duoduo.jxc.service.StyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/style")
@RequiredArgsConstructor
public class StyleController {

    private final StyleService styleService;

    @Log(title = "款式管理", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:style:view')")
    public Result<PageResult<StyleDTO>> page(StyleDTO query) {
        return Result.success(styleService.pageQuery(query));
    }

    @Log(title = "款式管理", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:style:view')")
    public Result<StyleDTO> getById(@PathVariable Long id) {
        return Result.success(styleService.getDetail(id));
    }

    @Log(title = "款式管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:style:add')")
    public Result<Long> add(@RequestBody StyleDTO dto) {
        return Result.success(styleService.create(dto));
    }

    @Log(title = "款式管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:style:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody StyleDTO dto) {
        dto.setStyleId(id);
        styleService.update(dto);
        return Result.success();
    }

    @Log(title = "款式管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:style:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        styleService.delete(id);
        return Result.success();
    }

    @Log(title = "款式管理", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:style:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        styleService.removeByIds(ids);
        return Result.success();
    }
}
