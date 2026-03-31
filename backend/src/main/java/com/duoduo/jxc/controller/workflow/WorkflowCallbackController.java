package com.duoduo.jxc.controller.workflow;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.workflow.WorkflowCallbackDTO;
import com.duoduo.jxc.service.WorkflowCallbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作流回调控制器
 */
@RestController
@RequestMapping("/workflow/callback")
@RequiredArgsConstructor
public class WorkflowCallbackController {

    private final WorkflowCallbackService callbackService;

    /**
     * 流程实例结束回调
     * POST /api/workflow/callback/process/completed
     */
    @PostMapping("/process/completed")
    public Result<Void> onProcessCompleted(@RequestBody WorkflowCallbackDTO dto) {
        callbackService.onProcessCompleted(dto);
        return Result.success();
    }

    /**
     * 任务通过回调
     * POST /api/workflow/callback/task/approved
     */
    @PostMapping("/task/approved")
    public Result<Void> onTaskApproved(@RequestBody WorkflowCallbackDTO dto) {
        callbackService.onTaskApproved(dto);
        return Result.success();
    }

    /**
     * 任务驳回回调
     * POST /api/workflow/callback/task/rejected
     */
    @PostMapping("/task/rejected")
    public Result<Void> onTaskRejected(@RequestBody WorkflowCallbackDTO dto) {
        callbackService.onTaskRejected(dto);
        return Result.success();
    }
}
