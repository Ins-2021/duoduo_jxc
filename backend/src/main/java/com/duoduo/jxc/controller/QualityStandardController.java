package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.QualityStandardDTO;
import com.duoduo.jxc.dto.QualityStandardQuery;
import com.duoduo.jxc.service.QualityStandardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qualitystandard")
@RequiredArgsConstructor
public class QualityStandardController {

    private final QualityStandardService qualityStandardService;

    @Log(title = "质检标准", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<QualityStandardDTO>> pageQuery(@Valid QualityStandardQuery query) {
        return Result.success(qualityStandardService.pageQuery(query));
    }

    @Log(title = "质检标准", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<QualityStandardDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(qualityStandardService.getDetail(id));
    }

    @Log(title = "质检标准", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@Valid @RequestBody QualityStandardDTO dto) {
        return Result.success(qualityStandardService.create(dto));
    }

    @Log(title = "质检标准", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody QualityStandardDTO dto) {
        dto.setStandardId(id);
        qualityStandardService.update(dto);
        return Result.success();
    }

    @Log(title = "质检标准", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        qualityStandardService.delete(id);
        return Result.success();
    }
}
