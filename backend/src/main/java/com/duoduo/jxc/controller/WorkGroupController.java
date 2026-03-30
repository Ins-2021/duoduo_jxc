package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.WorkGroupDTO;
import com.duoduo.jxc.service.WorkGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workgroup")
@RequiredArgsConstructor
public class WorkGroupController {

    private final WorkGroupService workGroupService;

    @Log(title = "班组管理", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:workgroup:view')")
    public Result<PageResult<WorkGroupDTO>> page(PageQuery query) {
        return Result.success(workGroupService.pageQuery(query));
    }

    @Log(title = "班组管理", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:workgroup:view')")
    public Result<WorkGroupDTO> getById(@PathVariable Long id) {
        return Result.success(workGroupService.getDetail(id));
    }

    @Log(title = "班组管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:workgroup:add')")
    public Result<Long> add(@RequestBody WorkGroupDTO dto) {
        return Result.success(workGroupService.create(dto));
    }

    @Log(title = "班组管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:workgroup:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody WorkGroupDTO dto) {
        dto.setGroupId(id);
        workGroupService.update(dto);
        return Result.success();
    }

    @Log(title = "班组管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:workgroup:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        workGroupService.delete(id);
        return Result.success();
    }

    @Log(title = "班组管理", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:workgroup:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        workGroupService.removeByIds(ids);
        return Result.success();
    }
}
