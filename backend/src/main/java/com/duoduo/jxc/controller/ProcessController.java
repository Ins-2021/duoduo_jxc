package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ProcessDTO;
import com.duoduo.jxc.dto.ProcessQuery;
import com.duoduo.jxc.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    @Log(title = "工序管理", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:process:view') or @perm.has('mes:records:view') or @perm.has('mes:scan:view') or @perm.has('mes:bundle:view')")
    public Result<PageResult<ProcessDTO>> pageQuery(@Valid ProcessQuery query) {
        return Result.success(processService.pageQuery(query));
    }

    @Log(title = "工序管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:view') or @perm.has('mes:records:view') or @perm.has('mes:scan:view') or @perm.has('mes:bundle:view')")
    public Result<ProcessDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(processService.getDetail(id));
    }

    @Log(title = "工序管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:process:add')")
    public Result<Long> create(@Valid @RequestBody ProcessDTO dto) {
        return Result.success(processService.create(dto));
    }

    @Log(title = "工序管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody ProcessDTO dto) {
        dto.setProcessId(id);
        processService.update(dto);
        return Result.success();
    }

    @Log(title = "工序管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        processService.delete(id);
        return Result.success();
    }
}
