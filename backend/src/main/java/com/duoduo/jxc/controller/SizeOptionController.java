package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.SizeOptionDTO;
import com.duoduo.jxc.service.SizeOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizeoption")
@RequiredArgsConstructor
public class SizeOptionController {

    private final SizeOptionService sizeOptionService;

    @Log(title = "尺码选项", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizeoption:view')")
    public Result<PageResult<SizeOptionDTO>> page(PageQuery query) {
        return Result.success(sizeOptionService.pageQuery(query));
    }

    @Log(title = "尺码选项", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizeoption:view')")
    public Result<SizeOptionDTO> getById(@PathVariable Long id) {
        return Result.success(sizeOptionService.getDetail(id));
    }

    @Log(title = "尺码选项", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:sizeoption:add')")
    public Result<Long> add(@RequestBody SizeOptionDTO dto) {
        return Result.success(sizeOptionService.create(dto));
    }

    @Log(title = "尺码选项", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizeoption:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SizeOptionDTO dto) {
        dto.setOptionId(id);
        sizeOptionService.update(dto);
        return Result.success();
    }

    @Log(title = "尺码选项", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizeoption:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sizeOptionService.delete(id);
        return Result.success();
    }

    @Log(title = "尺码选项", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:sizeoption:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        sizeOptionService.removeByIds(ids);
        return Result.success();
    }
}
