package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.AqlResultDTO;
import com.duoduo.jxc.dto.AqlStandardDTO;
import com.duoduo.jxc.dto.AqlStandardQuery;
import com.duoduo.jxc.service.AqlStandardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quality/aql")
@RequiredArgsConstructor
public class AqlStandardController {

    private final AqlStandardService aqlStandardService;

    @Log(title = "AQL标准", action = "计算抽检数量")
    @GetMapping("/calculate")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<AqlResultDTO> calculateSampleSize(
            @RequestParam Integer batchSize,
            @RequestParam String level) {
        return Result.success(aqlStandardService.calculateSampleSize(batchSize, level));
    }

    @Log(title = "AQL标准", action = "分页查询")
    @GetMapping
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<AqlStandardDTO>> pageQuery(@Valid AqlStandardQuery query) {
        return Result.success(aqlStandardService.pageQuery(query));
    }

    @Log(title = "AQL标准", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<AqlStandardDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(aqlStandardService.getDetail(id));
    }

    @Log(title = "AQL标准", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@Valid @RequestBody AqlStandardDTO dto) {
        return Result.success(aqlStandardService.create(dto));
    }

    @Log(title = "AQL标准", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody AqlStandardDTO dto) {
        dto.setAqlId(id);
        aqlStandardService.update(dto);
        return Result.success();
    }

    @Log(title = "AQL标准", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        aqlStandardService.delete(id);
        return Result.success();
    }
}
