package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.dto.ScanRecordQuery;
import com.duoduo.jxc.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mes/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @Log(title = "扫码计件", action = "扫码计件")
    @PostMapping
    @PreAuthorize("@perm.has('mes:scan:add')")
    public Result<ScanRecordDTO> scan(@RequestBody ScanRecordDTO dto) {
        return Result.success(scanService.scan(dto));
    }

    @Log(title = "扫码计件", action = "二维码扫码计件")
    @PostMapping("/qrcode")
    @PreAuthorize("@perm.has('mes:scan:add')")
    public Result<ScanRecordDTO> scanWithQrCode(@RequestParam String qrCodeNo,
                                                 @RequestParam Long workerId,
                                                 @RequestParam Long processId,
                                                 @RequestParam(required = false, defaultValue = "1") Integer quantity) {
        return Result.success(scanService.scanWithQrCode(qrCodeNo, workerId, processId, quantity));
    }

    @Log(title = "扫码计件", action = "分页查询扫码记录")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('mes:records:view')")
    public Result<PageResult<ScanRecordDTO>> pageQuery(ScanRecordQuery query) {
        return Result.success(scanService.pageQuery(query));
    }

    @Log(title = "扫码计件", action = "查询今日扫码记录")
    @GetMapping("/today/{workerId}")
    @PreAuthorize("@perm.has('mes:records:view')")
    public Result<List<ScanRecordDTO>> getTodayRecords(@PathVariable Long workerId) {
        return Result.success(scanService.getTodayRecordsByWorker(workerId));
    }

    @Log(title = "扫码计件", action = "查看扫码记录详情")
    @GetMapping("/{recordId}")
    @PreAuthorize("@perm.has('mes:records:view')")
    public Result<ScanRecordDTO> getDetail(@PathVariable Long recordId) {
        return Result.success(scanService.getDetail(recordId));
    }

    @Log(title = "扫码计件", action = "确认扫码记录")
    @PostMapping("/{recordId}/confirm")
    @PreAuthorize("@perm.has('mes:scan:edit')")
    public Result<Void> confirm(@PathVariable Long recordId) {
        scanService.confirm(recordId);
        return Result.success();
    }

    @Log(title = "扫码计件", action = "驳回扫码记录")
    @PostMapping("/{recordId}/reject")
    @PreAuthorize("@perm.has('mes:scan:edit')")
    public Result<Void> reject(@PathVariable Long recordId, @RequestParam(required = false) String reason) {
        scanService.reject(recordId, reason);
        return Result.success();
    }
}
