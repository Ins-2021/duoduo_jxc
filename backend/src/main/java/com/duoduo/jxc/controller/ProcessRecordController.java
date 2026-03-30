package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.BundleFlowDTO;
import com.duoduo.jxc.dto.BundleTransferDTO;
import com.duoduo.jxc.dto.ProcessRecordDTO;
import com.duoduo.jxc.dto.ProcessRecordQuery;
import com.duoduo.jxc.dto.ProcessRecordShareDTO;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.service.ProcessRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processes/records")
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

    @Log(title = "扫码计件", action = "扫码计件")
    @PostMapping("/scan")
    @PreAuthorize("@perm.has('mes:process:scan')")
    public Result<ProcessRecordDTO> scanRecord(@Valid @RequestBody ScanRecordDTO dto) {
        return Result.success(processRecordService.scanRecord(dto));
    }

    @Log(title = "多人分成", action = "添加分成")
    @PostMapping("/{recordId}/shares")
    @PreAuthorize("@perm.has('mes:process:edit')")
    public Result<Void> addShares(@PathVariable("recordId") Long recordId, 
                                   @Valid @RequestBody List<ProcessRecordShareDTO> shares) {
        processRecordService.addShares(recordId, shares);
        return Result.success();
    }

    @Log(title = "扎包流转", action = "查询流转信息")
    @GetMapping("/bundles/{bundleId}/flow")
    @PreAuthorize("@perm.has('mes:bundle:view')")
    public Result<BundleFlowDTO> getBundleFlow(@PathVariable("bundleId") Long bundleId) {
        return Result.success(processRecordService.getBundleFlow(bundleId));
    }

    @Log(title = "扎包交接", action = "扎包交接")
    @PostMapping("/bundles/transfer")
    @PreAuthorize("@perm.has('mes:bundle:transfer')")
    public Result<Void> transferBundle(@Valid @RequestBody BundleTransferDTO dto) {
        processRecordService.transferBundle(dto);
        return Result.success();
    }
}
