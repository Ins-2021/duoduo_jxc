package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.QualityCheckDTO;
import com.duoduo.jxc.dto.QualityCheckQuery;
import com.duoduo.jxc.service.QualityCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qualitycheck")
@RequiredArgsConstructor
public class QualityCheckController {

    private final QualityCheckService qualityCheckService;

    @Log(title = "质检记录", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<QualityCheckDTO>> pageQuery(@Valid QualityCheckQuery query) {
        return Result.success(qualityCheckService.pageQuery(query));
    }

    @Log(title = "质检记录", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<QualityCheckDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(qualityCheckService.getDetail(id));
    }

    @Log(title = "质检记录", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@Valid @RequestBody QualityCheckDTO dto) {
        return Result.success(qualityCheckService.create(dto));
    }

    @Log(title = "质检记录", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody QualityCheckDTO dto) {
        dto.setCheckId(id);
        qualityCheckService.update(dto);
        return Result.success();
    }

    @Log(title = "质检记录", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        qualityCheckService.delete(id);
        return Result.success();
    }
}
