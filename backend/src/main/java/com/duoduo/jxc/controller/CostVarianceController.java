package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.CostVarianceDTO;
import com.duoduo.jxc.dto.cost.CostVarianceQuery;
import com.duoduo.jxc.service.CostVarianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/variance")
@RequiredArgsConstructor
public class CostVarianceController {

    private final CostVarianceService costVarianceService;

    @Log(title = "成本差异分析", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:variance:view')")
    public Result<PageResult<CostVarianceDTO>> page(CostVarianceQuery query) {
        return Result.success(costVarianceService.pageQuery(query));
    }

    @Log(title = "成本差异分析", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:variance:view')")
    public Result<CostVarianceDTO> getById(@PathVariable Long id) {
        return Result.success(costVarianceService.getDetail(id));
    }

    @Log(title = "成本差异分析", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('cost:variance:add')")
    public Result<Long> create(@RequestBody CostVarianceDTO dto) {
        return Result.success(costVarianceService.create(dto));
    }

    @Log(title = "成本差异分析", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:variance:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CostVarianceDTO dto) {
        dto.setVarianceId(id);
        costVarianceService.update(dto);
        return Result.success();
    }

    @Log(title = "成本差异分析", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:variance:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        costVarianceService.delete(id);
        return Result.success();
    }

    @Log(title = "成本差异分析", action = "标记已分析")
    @PutMapping("/{id}/analyze")
    @PreAuthorize("@perm.has('cost:variance:edit')")
    public Result<Void> analyze(@PathVariable Long id) {
        costVarianceService.analyze(id);
        return Result.success();
    }
}
