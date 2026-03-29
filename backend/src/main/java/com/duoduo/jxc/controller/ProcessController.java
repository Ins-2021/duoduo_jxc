package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ProcessDTO;
import com.duoduo.jxc.dto.ProcessQuery;
import com.duoduo.jxc.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<PageResult<ProcessDTO>> pageQuery(ProcessQuery query) {
        return Result.success(processService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<ProcessDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(processService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('mes:process:add')")
    public Result<Long> create(@RequestBody ProcessDTO dto) {
        return Result.success(processService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody ProcessDTO dto) {
        dto.setProcessId(id);
        processService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        processService.delete(id);
        return Result.success();
    }
}
