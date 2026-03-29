package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ProcessRecordDTO;
import com.duoduo.jxc.dto.ProcessRecordQuery;
import com.duoduo.jxc.service.ProcessRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processrecord")
@RequiredArgsConstructor
public class ProcessRecordController {

    private final ProcessRecordService processRecordService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<PageResult<ProcessRecordDTO>> pageQuery(ProcessRecordQuery query) {
        return Result.success(processRecordService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<ProcessRecordDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(processRecordService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('mes:process:add')")
    public Result<Long> create(@RequestBody ProcessRecordDTO dto) {
        return Result.success(processRecordService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody ProcessRecordDTO dto) {
        dto.setRecordId(id);
        processRecordService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        processRecordService.delete(id);
        return Result.success();
    }
}
