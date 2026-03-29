package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.BundleDTO;
import com.duoduo.jxc.dto.BundleQuery;
import com.duoduo.jxc.service.BundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bundle")
@RequiredArgsConstructor
public class BundleController {

    private final BundleService bundleService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<PageResult<BundleDTO>> pageQuery(BundleQuery query) {
        return Result.success(bundleService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<BundleDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(bundleService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('mes:process:add')")
    public Result<Long> create(@RequestBody BundleDTO dto) {
        return Result.success(bundleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody BundleDTO dto) {
        dto.setBundleId(id);
        bundleService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        bundleService.delete(id);
        return Result.success();
    }
}
