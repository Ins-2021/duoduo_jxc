package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.FirstArticleConfirmationDTO;
import com.duoduo.jxc.dto.FirstArticleConfirmationQuery;
import com.duoduo.jxc.service.FirstArticleConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firstarticleconfirmation")
@RequiredArgsConstructor
public class FirstArticleConfirmationController {

    private final FirstArticleConfirmationService firstArticleConfirmationService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<FirstArticleConfirmationDTO>> pageQuery(FirstArticleConfirmationQuery query) {
        return Result.success(firstArticleConfirmationService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<FirstArticleConfirmationDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(firstArticleConfirmationService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@RequestBody FirstArticleConfirmationDTO dto) {
        return Result.success(firstArticleConfirmationService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody FirstArticleConfirmationDTO dto) {
        dto.setConfirmationId(id);
        firstArticleConfirmationService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        firstArticleConfirmationService.delete(id);
        return Result.success();
    }
}
