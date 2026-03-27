package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.workflow.WfBindingSaveRequest;
import com.duoduo.jxc.entity.workflow.WfBizProcessBinding;
import com.duoduo.jxc.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workflow/bindings")
@RequiredArgsConstructor
public class WorkflowBindingController {

    private final WorkflowService workflowService;

    @GetMapping
    @PreAuthorize("@perm.has('workflow:binding:view')")
    public Result<WfBizProcessBinding> get(@RequestParam String bizType) {
        return Result.success(workflowService.getBinding(bizType));
    }

    @PutMapping("/{bizType}")
    @PreAuthorize("@perm.has('workflow:binding:edit')")
    public Result<Void> save(@PathVariable String bizType,
                             @RequestBody @Validated WfBindingSaveRequest request,
                             @RequestParam(required = false) Long operatorId) {
        workflowService.saveBinding(bizType, request, operatorId);
        return Result.success();
    }
}
