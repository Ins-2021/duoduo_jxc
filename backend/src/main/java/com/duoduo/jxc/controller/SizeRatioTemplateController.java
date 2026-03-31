package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.SizeRatioTemplateDTO;
import com.duoduo.jxc.service.SizeRatioTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sizeratiotemplate")
@RequiredArgsConstructor
public class SizeRatioTemplateController {

    private final SizeRatioTemplateService sizeRatioTemplateService;

    @Log(title = "尺码配比模板", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizeratiotemplate:view')")
    public Result<PageResult<SizeRatioTemplateDTO>> page(PageQuery query) {
        return Result.success(sizeRatioTemplateService.pageQuery(query));
    }

    @Log(title = "尺码配比模板", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:sizeratiotemplate:view')")
    public Result<SizeRatioTemplateDTO> getById(@PathVariable Long id) {
        return Result.success(sizeRatioTemplateService.getDetail(id));
    }

    @Log(title = "尺码配比模板", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:sizeratiotemplate:add')")
    public Result<Long> add(@RequestBody SizeRatioTemplateDTO dto) {
        return Result.success(sizeRatioTemplateService.create(dto));
    }

    @Log(title = "尺码配比模板", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizeratiotemplate:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SizeRatioTemplateDTO dto) {
        dto.setTemplateId(id);
        sizeRatioTemplateService.update(dto);
        return Result.success();
    }

    @Log(title = "尺码配比模板", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:sizeratiotemplate:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sizeRatioTemplateService.delete(id);
        return Result.success();
    }

    @Log(title = "尺码配比模板", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:sizeratiotemplate:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        sizeRatioTemplateService.removeByIds(ids);
        return Result.success();
    }
}
