package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.PatrolCheckDTO;
import com.duoduo.jxc.dto.PatrolCheckQuery;
import com.duoduo.jxc.service.PatrolCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quality/patrols")
@RequiredArgsConstructor
public class PatrolCheckController {

    private final PatrolCheckService patrolCheckService;

    @Log(title = "巡检记录", action = "分页查询")
    @GetMapping
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<PatrolCheckDTO>> pageQuery(@Valid PatrolCheckQuery query) {
        return Result.success(patrolCheckService.pageQuery(query));
    }

    @Log(title = "巡检记录", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PatrolCheckDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(patrolCheckService.getDetail(id));
    }

    @Log(title = "巡检记录", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@Valid @RequestBody PatrolCheckDTO dto) {
        return Result.success(patrolCheckService.create(dto));
    }

    @Log(title = "巡检记录", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody PatrolCheckDTO dto) {
        dto.setPatrolId(id);
        patrolCheckService.update(dto);
        return Result.success();
    }

    @Log(title = "巡检记录", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        patrolCheckService.delete(id);
        return Result.success();
    }
}
