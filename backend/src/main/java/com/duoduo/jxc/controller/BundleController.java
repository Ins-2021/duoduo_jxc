package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.BundleDTO;
import com.duoduo.jxc.dto.BundleQuery;
import com.duoduo.jxc.service.BundleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bundle")
@RequiredArgsConstructor
public class BundleController {

    private final BundleService bundleService;

    @Log(title = "扎包管理", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<PageResult<BundleDTO>> pageQuery(@Valid BundleQuery query) {
        return Result.success(bundleService.pageQuery(query));
    }

    @Log(title = "扎包管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<BundleDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(bundleService.getDetail(id));
    }

    @Log(title = "扎包管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:process:add')")
    public Result<Long> create(@Valid @RequestBody BundleDTO dto) {
        return Result.success(bundleService.create(dto));
    }

    @Log(title = "扎包管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody BundleDTO dto) {
        dto.setBundleId(id);
        bundleService.update(dto);
        return Result.success();
    }

    @Log(title = "扎包管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        bundleService.delete(id);
        return Result.success();
    }
}
