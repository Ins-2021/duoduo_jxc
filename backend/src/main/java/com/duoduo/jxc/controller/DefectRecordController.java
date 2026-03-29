package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.DefectRecordDTO;
import com.duoduo.jxc.dto.DefectRecordQuery;
import com.duoduo.jxc.service.DefectRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/defectrecord")
@RequiredArgsConstructor
public class DefectRecordController {

    private final DefectRecordService defectRecordService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<DefectRecordDTO>> pageQuery(DefectRecordQuery query) {
        return Result.success(defectRecordService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<DefectRecordDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(defectRecordService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@RequestBody DefectRecordDTO dto) {
        return Result.success(defectRecordService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody DefectRecordDTO dto) {
        dto.setDefectId(id);
        defectRecordService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        defectRecordService.delete(id);
        return Result.success();
    }
}
