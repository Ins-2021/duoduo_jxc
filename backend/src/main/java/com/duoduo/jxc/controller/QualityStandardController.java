package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.QualityStandardDTO;
import com.duoduo.jxc.dto.QualityStandardQuery;
import com.duoduo.jxc.service.QualityStandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qualitystandard")
@RequiredArgsConstructor
public class QualityStandardController {

    private final QualityStandardService qualityStandardService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<QualityStandardDTO>> pageQuery(QualityStandardQuery query) {
        return Result.success(qualityStandardService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<QualityStandardDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(qualityStandardService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@RequestBody QualityStandardDTO dto) {
        return Result.success(qualityStandardService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody QualityStandardDTO dto) {
        dto.setStandardId(id);
        qualityStandardService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        qualityStandardService.delete(id);
        return Result.success();
    }
}
