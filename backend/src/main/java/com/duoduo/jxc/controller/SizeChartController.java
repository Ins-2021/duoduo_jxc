package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.SizeChartDTO;
import com.duoduo.jxc.service.SizeChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizechart")
@RequiredArgsConstructor
public class SizeChartController {

    private final SizeChartService sizeChartService;

    @Log(title = "尺码表", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizechart:view')")
    public Result<PageResult<SizeChartDTO>> page(PageQuery query) {
        return Result.success(sizeChartService.pageQuery(query));
    }

    @Log(title = "尺码表", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizechart:view')")
    public Result<SizeChartDTO> getById(@PathVariable Long id) {
        return Result.success(sizeChartService.getDetail(id));
    }

    @Log(title = "尺码表", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:sizechart:add')")
    public Result<Long> add(@RequestBody SizeChartDTO dto) {
        return Result.success(sizeChartService.create(dto));
    }

    @Log(title = "尺码表", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizechart:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SizeChartDTO dto) {
        dto.setChartId(id);
        sizeChartService.update(dto);
        return Result.success();
    }

    @Log(title = "尺码表", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizechart:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sizeChartService.delete(id);
        return Result.success();
    }

    @Log(title = "尺码表", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:sizechart:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        sizeChartService.removeByIds(ids);
        return Result.success();
    }
}
