package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.StyleColorwayDTO;
import com.duoduo.jxc.service.StyleColorwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stylecolorway")
@RequiredArgsConstructor
public class StyleColorwayController {

    private final StyleColorwayService styleColorwayService;

    @Log(title = "款式配色", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:stylecolorway:view')")
    public Result<PageResult<StyleColorwayDTO>> page(StyleColorwayDTO query) {
        return Result.success(styleColorwayService.pageQuery(query));
    }

    @Log(title = "款式配色", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:stylecolorway:view')")
    public Result<StyleColorwayDTO> getById(@PathVariable Long id) {
        return Result.success(styleColorwayService.getDetail(id));
    }

    @Log(title = "款式配色", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:stylecolorway:add')")
    public Result<Long> add(@RequestBody StyleColorwayDTO dto) {
        return Result.success(styleColorwayService.create(dto));
    }

    @Log(title = "款式配色", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:stylecolorway:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody StyleColorwayDTO dto) {
        dto.setColorwayId(id);
        styleColorwayService.update(dto);
        return Result.success();
    }

    @Log(title = "款式配色", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:stylecolorway:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        styleColorwayService.delete(id);
        return Result.success();
    }

    @Log(title = "款式配色", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:stylecolorway:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        styleColorwayService.removeByIds(ids);
        return Result.success();
    }
}
