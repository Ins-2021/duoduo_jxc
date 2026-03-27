package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.workflow.WfDiagramStateResponse;
import com.duoduo.jxc.dto.workflow.WfInstanceStartRequest;
import com.duoduo.jxc.entity.workflow.WfInstance;
import com.duoduo.jxc.service.WorkflowService;
import com.duoduo.jxc.security.SecurityUtils;
import com.duoduo.jxc.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflow/instances")
@RequiredArgsConstructor
public class WorkflowInstanceController {

    private final WorkflowService workflowService;

    @PostMapping("/start")
    @PreAuthorize("@perm.has('workflow:instance:start')")
    public Result<Map<String, Object>> start(@RequestBody @Validated WfInstanceStartRequest request) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        request.setInitiatorId(userId);
        return Result.success(workflowService.startInstance(request));
    }

    @GetMapping("/mine")
    @PreAuthorize("@perm.has('workflow:instance:mine')")
    public Result<List<WfInstance>> mine() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        return Result.success(workflowService.listMyInstances(userId));
    }

    @GetMapping("/{procInstId}")
    @PreAuthorize("@perm.has('workflow:instance:mine')")
    public Result<Map<String, Object>> detail(@PathVariable String procInstId) {
        return Result.success(workflowService.getInstanceDetail(procInstId));
    }

    @GetMapping("/{procInstId}/diagram")
    @PreAuthorize("@perm.has('workflow:instance:mine')")
    public Result<String> diagram(@PathVariable String procInstId) {
        return Result.success(workflowService.getDiagramXml(procInstId));
    }

    @GetMapping("/{procInstId}/diagram-state")
    @PreAuthorize("@perm.has('workflow:instance:mine')")
    public Result<WfDiagramStateResponse> diagramState(@PathVariable String procInstId) {
        return Result.success(workflowService.getDiagramState(procInstId));
    }
}
