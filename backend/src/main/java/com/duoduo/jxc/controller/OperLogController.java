package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.oplog.OperLogDTO;
import com.duoduo.jxc.dto.oplog.OperLogQuery;
import com.duoduo.jxc.service.OperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/op-log")
@RequiredArgsConstructor
public class OperLogController {
    private final OperLogService operLogService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('settings:op-log:view')")
    public Result<PageResult<OperLogDTO>> page(OperLogQuery query) {
        return Result.success(operLogService.pageQuery(query));
    }

    @Log(title = "操作日志", action = "清空操作日志")
    @DeleteMapping("/clear")
    @PreAuthorize("@perm.has('settings:op-log:edit')")
    public Result<Void> clear() {
        operLogService.clearAll();
        return Result.success();
    }
}

