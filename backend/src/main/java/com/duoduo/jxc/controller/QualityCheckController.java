package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.QualityCheckDTO;
import com.duoduo.jxc.dto.QualityCheckQuery;
import com.duoduo.jxc.dto.QualityCheckSubmitDTO;
import com.duoduo.jxc.dto.ReworkOrderDTO;
import com.duoduo.jxc.service.QualityCheckService;
import com.duoduo.jxc.service.ReworkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quality/checks")
@RequiredArgsConstructor
public class QualityCheckController {

    private final QualityCheckService qualityCheckService;
    private final ReworkOrderService reworkOrderService;

    @Log(title = "质检记录", action = "提交质检结果")
    @PostMapping
    @PreAuthorize("@perm.has('mes:quality:add')")
    public Result<Long> submitCheckResult(@Valid @RequestBody QualityCheckSubmitDTO dto) {
        return Result.success(qualityCheckService.submitCheckResult(dto));
    }

    @Log(title = "质检记录", action = "查询关联返工单")
    @GetMapping("/{id}/rework")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<ReworkOrderDTO> getReworkOrder(@PathVariable("id") Long id) {
        return Result.success(reworkOrderService.getByCheckId(id));
    }

    @Log(title = "质检记录", action = "分页查询")
    @GetMapping
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<PageResult<QualityCheckDTO>> pageQuery(@Valid QualityCheckQuery query) {
        return Result.success(qualityCheckService.pageQuery(query));
    }

    @Log(title = "质检记录", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:view')")
    public Result<QualityCheckDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(qualityCheckService.getDetail(id));
    }

    @Log(title = "质检记录", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody QualityCheckDTO dto) {
        dto.setCheckId(id);
        qualityCheckService.update(dto);
        return Result.success();
    }

    @Log(title = "质检记录", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('mes:quality:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        qualityCheckService.delete(id);
        return Result.success();
    }
}
