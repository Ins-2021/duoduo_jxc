package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.workflow.WfModelCreateRequest;
import com.duoduo.jxc.dto.workflow.WfModelPublishRequest;
import com.duoduo.jxc.dto.workflow.WfModelSaveRequest;
import com.duoduo.jxc.entity.workflow.WfModel;
import com.duoduo.jxc.entity.workflow.WfModelPublish;
import com.duoduo.jxc.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflow/models")
@RequiredArgsConstructor
public class WorkflowModelController {

    private final WorkflowService workflowService;

    @GetMapping
    @PreAuthorize("@perm.has('workflow:model:view')")
    public Result<List<WfModel>> list() {
        return Result.success(workflowService.listModels());
    }

    @PostMapping
    @PreAuthorize("@perm.has('workflow:model:add')")
    public Result<Long> create(@RequestBody @Validated WfModelCreateRequest request,
                               @RequestParam(required = false) Long operatorId) {
        return Result.success(workflowService.createModel(request, operatorId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('workflow:model:view')")
    public Result<WfModel> get(@PathVariable Long id) {
        return Result.success(workflowService.getModel(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('workflow:model:edit')")
    public Result<Void> save(@PathVariable Long id,
                             @RequestBody @Validated WfModelSaveRequest request,
                             @RequestParam(required = false) Long operatorId) {
        workflowService.saveModel(id, request, operatorId);
        return Result.success();
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("@perm.has('workflow:model:view')")
    public Result<Void> validate(@PathVariable Long id) {
        workflowService.validateModel(id);
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("@perm.has('workflow:model:publish')")
    public Result<WfModelPublish> publish(@PathVariable Long id,
                                          @RequestBody(required = false) WfModelPublishRequest request) {
        return Result.success(workflowService.publishModel(id, request));
    }
}
