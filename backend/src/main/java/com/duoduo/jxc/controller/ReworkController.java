package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ReworkOrderDTO;
import com.duoduo.jxc.dto.ReworkOrderQuery;
import com.duoduo.jxc.service.ReworkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quality/reworks")
@RequiredArgsConstructor
public class ReworkController {

    private final ReworkOrderService reworkOrderService;

    @Log(title = "返工单", action = "分页查询")
    @GetMapping
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<ReworkOrderDTO>> pageQuery(@Valid ReworkOrderQuery query) {
        return Result.success(reworkOrderService.pageQuery(query));
    }

    @Log(title = "返工单", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<ReworkOrderDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(reworkOrderService.getDetail(id));
    }

    @Log(title = "返工单", action = "根据质检单查询")
    @GetMapping("/by-check/{checkId}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<ReworkOrderDTO> getByCheckId(@PathVariable("checkId") Long checkId) {
        return Result.success(reworkOrderService.getByCheckId(checkId));
    }

    @Log(title = "返工单", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> create(@Valid @RequestBody ReworkOrderDTO dto) {
        return Result.success(reworkOrderService.create(dto));
    }

    @Log(title = "返工单", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody ReworkOrderDTO dto) {
        dto.setReworkId(id);
        reworkOrderService.update(dto);
        return Result.success();
    }

    @Log(title = "返工单", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        reworkOrderService.delete(id);
        return Result.success();
    }
}
