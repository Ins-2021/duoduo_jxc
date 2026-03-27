package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.workflow.WfTaskApproveRequest;
import com.duoduo.jxc.dto.workflow.WfTaskRejectRequest;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.security.SecurityUtils;
import com.duoduo.jxc.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workflow/tasks")
@RequiredArgsConstructor
public class WorkflowTaskController {

    private final WorkflowService workflowService;

    @GetMapping("/todo")
    @PreAuthorize("@perm.has('workflow:task:todo')")
    public Result<List<Map<String, Object>>> todo() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        return Result.success(workflowService.getTodoTasks(userId));
    }

    @GetMapping("/done")
    @PreAuthorize("@perm.has('workflow:task:done')")
    public Result<List<Map<String, Object>>> done() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        return Result.success(workflowService.getDoneTasks(userId));
    }

    @PostMapping("/{taskId}/approve")
    @PreAuthorize("@perm.has('workflow:task:approve')")
    public Result<Void> approve(@PathVariable String taskId,
                                @RequestBody @Validated WfTaskApproveRequest request) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        request.setUserId(userId);
        workflowService.approveTask(taskId, request);
        return Result.success();
    }

    @PostMapping("/{taskId}/reject")
    @PreAuthorize("@perm.has('workflow:task:reject')")
    public Result<Void> reject(@PathVariable String taskId,
                               @RequestBody @Validated WfTaskRejectRequest request) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(BizCode.UNAUTHORIZED);
        }
        request.setUserId(userId);
        workflowService.rejectTask(taskId, request);
        return Result.success();
    }
}
