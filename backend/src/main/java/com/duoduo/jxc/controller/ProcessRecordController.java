package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ProcessRecordDTO;
import com.duoduo.jxc.dto.ProcessRecordQuery;
import com.duoduo.jxc.service.ProcessRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processrecord")
@RequiredArgsConstructor
public class ProcessRecordController {

    private final ProcessRecordService processRecordService;

    @Log(title = "工序报工", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<PageResult<ProcessRecordDTO>> pageQuery(@Valid ProcessRecordQuery query) {
        return Result.success(processRecordService.pageQuery(query));
    }

    @Log(title = "工序报工", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:view')")
    public Result<ProcessRecordDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(processRecordService.getDetail(id));
    }

    @Log(title = "工序报工", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:process:add')")
    public Result<Long> create(@Valid @RequestBody ProcessRecordDTO dto) {
        return Result.success(processRecordService.create(dto));
    }

    @Log(title = "工序报工", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody ProcessRecordDTO dto) {
        dto.setRecordId(id);
        processRecordService.update(dto);
        return Result.success();
    }

    @Log(title = "工序报工", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:process:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        processRecordService.delete(id);
        return Result.success();
    }
}
