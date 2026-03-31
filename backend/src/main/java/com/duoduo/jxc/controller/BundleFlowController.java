package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.BundleFlowDTO;
import com.duoduo.jxc.dto.BundleTransferDTO;
import com.duoduo.jxc.service.BundleFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mes/flow")
@RequiredArgsConstructor
public class BundleFlowController {

    private final BundleFlowService bundleFlowService;

    @Log(title = "扎包流转", action = "扎包交接")
    @PostMapping("/transfer")
    @PreAuthorize("@perm.has('mes:flow:edit')")
    public Result<Void> transfer(@RequestBody BundleTransferDTO dto) {
        bundleFlowService.recordTransfer(dto);
        return Result.success();
    }

    @Log(title = "扎包流转", action = "查询扎包流转历史")
    @GetMapping("/history/{bundleId}")
    @PreAuthorize("@perm.has('mes:flow:view')")
    public Result<List<BundleFlowDTO>> getHistory(@PathVariable Long bundleId) {
        return Result.success(bundleFlowService.getBundleFlowHistory(bundleId));
    }

    @Log(title = "扎包流转", action = "查询最新流转记录")
    @GetMapping("/latest/{bundleId}")
    @PreAuthorize("@perm.has('mes:flow:view')")
    public Result<BundleFlowDTO> getLatest(@PathVariable Long bundleId) {
        return Result.success(bundleFlowService.getLatestFlow(bundleId));
    }
}
