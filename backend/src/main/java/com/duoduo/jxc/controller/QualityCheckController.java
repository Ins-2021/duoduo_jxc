package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.QualityCheckDTO;
import com.duoduo.jxc.dto.QualityCheckQuery;
import com.duoduo.jxc.service.QualityCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qualitycheck")
@RequiredArgsConstructor
public class QualityCheckController {

    private final QualityCheckService qualityCheckService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<QualityCheckDTO>> pageQuery(QualityCheckQuery query) {
        return Result.success(qualityCheckService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<QualityCheckDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(qualityCheckService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@RequestBody QualityCheckDTO dto) {
        return Result.success(qualityCheckService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody QualityCheckDTO dto) {
        dto.setCheckId(id);
        qualityCheckService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        qualityCheckService.delete(id);
        return Result.success();
    }
}
