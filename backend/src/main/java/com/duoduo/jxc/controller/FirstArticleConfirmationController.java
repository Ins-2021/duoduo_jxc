package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.FirstArticleConfirmationDTO;
import com.duoduo.jxc.dto.FirstArticleConfirmationQuery;
import com.duoduo.jxc.service.FirstArticleConfirmationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firstarticleconfirmation")
@RequiredArgsConstructor
public class FirstArticleConfirmationController {

    private final FirstArticleConfirmationService firstArticleConfirmationService;

    @Log(title = "首件确认", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<FirstArticleConfirmationDTO>> pageQuery(@Valid FirstArticleConfirmationQuery query) {
        return Result.success(firstArticleConfirmationService.pageQuery(query));
    }

    @Log(title = "首件确认", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<FirstArticleConfirmationDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(firstArticleConfirmationService.getDetail(id));
    }

    @Log(title = "首件确认", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@Valid @RequestBody FirstArticleConfirmationDTO dto) {
        return Result.success(firstArticleConfirmationService.create(dto));
    }

    @Log(title = "首件确认", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody FirstArticleConfirmationDTO dto) {
        dto.setConfirmationId(id);
        firstArticleConfirmationService.update(dto);
        return Result.success();
    }

    @Log(title = "首件确认", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        firstArticleConfirmationService.delete(id);
        return Result.success();
    }
}
