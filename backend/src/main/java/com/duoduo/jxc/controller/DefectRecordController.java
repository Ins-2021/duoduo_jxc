package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.DefectRecordDTO;
import com.duoduo.jxc.dto.DefectRecordQuery;
import com.duoduo.jxc.service.DefectRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/defectrecord")
@RequiredArgsConstructor
public class DefectRecordController {

    private final DefectRecordService defectRecordService;

    @Log(title = "返工记录", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<DefectRecordDTO>> pageQuery(@Valid DefectRecordQuery query) {
        return Result.success(defectRecordService.pageQuery(query));
    }

    @Log(title = "返工记录", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<DefectRecordDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(defectRecordService.getDetail(id));
    }

    @Log(title = "返工记录", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@Valid @RequestBody DefectRecordDTO dto) {
        return Result.success(defectRecordService.create(dto));
    }

    @Log(title = "返工记录", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody DefectRecordDTO dto) {
        dto.setDefectId(id);
        defectRecordService.update(dto);
        return Result.success();
    }

    @Log(title = "返工记录", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        defectRecordService.delete(id);
        return Result.success();
    }
}
