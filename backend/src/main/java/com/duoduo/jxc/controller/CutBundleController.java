package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.production.CutBundleDTO;
import com.duoduo.jxc.dto.production.CutBundleQuery;
import com.duoduo.jxc.service.CutBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production/cut-bundle")
@RequiredArgsConstructor
public class CutBundleController {

    private final CutBundleService cutBundleService;

    @Log(title = "裁床扎包", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('production:cut-bundle:view')")
    public Result<PageResult<CutBundleDTO>> page(CutBundleQuery query) {
        return Result.success(cutBundleService.pageQuery(query));
    }

    @Log(title = "裁床扎包", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('production:cut-bundle:view')")
    public Result<CutBundleDTO> getById(@PathVariable Long id) {
        return Result.success(cutBundleService.getDetail(id));
    }

    @Log(title = "裁床扎包", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('production:cut-bundle:add')")
    public Result<Long> create(@RequestBody CutBundleDTO dto) {
        return Result.success(cutBundleService.create(dto));
    }

    @Log(title = "裁床扎包", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('production:cut-bundle:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CutBundleDTO dto) {
        dto.setBundleId(id);
        cutBundleService.update(dto);
        return Result.success();
    }

    @Log(title = "裁床扎包", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('production:cut-bundle:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        cutBundleService.delete(id);
        return Result.success();
    }

    @Log(title = "裁床扎包", action = "更新状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('production:cut-bundle:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        cutBundleService.updateStatus(id, status);
        return Result.success();
    }

    @Log(title = "裁床扎包", action = "分配工序")
    @PutMapping("/{id}/assign")
    @PreAuthorize("@perm.has('production:cut-bundle:edit')")
    public Result<Void> assignProcess(@PathVariable Long id, @RequestParam Long processId) {
        cutBundleService.assignProcess(id, processId);
        return Result.success();
    }
}
