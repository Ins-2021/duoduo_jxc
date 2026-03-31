package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.WorkshopDTO;
import com.duoduo.jxc.service.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workshop")
@RequiredArgsConstructor
public class WorkshopController {

    private final WorkshopService workshopService;

    @Log(title = "车间管理", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:workshop:view')")
    public Result<PageResult<WorkshopDTO>> page(PageQuery query) {
        return Result.success(workshopService.pageQuery(query));
    }

    @Log(title = "车间管理", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:workshop:view')")
    public Result<WorkshopDTO> getById(@PathVariable Long id) {
        return Result.success(workshopService.getDetail(id));
    }

    @Log(title = "车间管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:workshop:add')")
    public Result<Long> add(@RequestBody WorkshopDTO dto) {
        return Result.success(workshopService.create(dto));
    }

    @Log(title = "车间管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('data:workshop:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody WorkshopDTO dto) {
        dto.setWorkshopId(id);
        workshopService.update(dto);
        return Result.success();
    }

    @Log(title = "车间管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:workshop:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        workshopService.delete(id);
        return Result.success();
    }

    @Log(title = "车间管理", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:workshop:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        workshopService.removeByIds(ids);
        return Result.success();
    }
}
