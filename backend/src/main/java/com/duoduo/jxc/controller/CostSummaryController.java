package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.CostSummaryDTO;
import com.duoduo.jxc.dto.cost.CostSummaryQuery;
import com.duoduo.jxc.service.CostSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/summary")
@RequiredArgsConstructor
public class CostSummaryController {

    private final CostSummaryService costSummaryService;

    @Log(title = "成本汇总", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:summary:view')")
    public Result<PageResult<CostSummaryDTO>> page(CostSummaryQuery query) {
        return Result.success(costSummaryService.pageQuery(query));
    }

    @Log(title = "成本汇总", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:summary:view')")
    public Result<CostSummaryDTO> getById(@PathVariable Long id) {
        return Result.success(costSummaryService.getDetail(id));
    }

    @Log(title = "成本汇总", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('cost:summary:add')")
    public Result<Long> create(@RequestBody CostSummaryDTO dto) {
        return Result.success(costSummaryService.create(dto));
    }

    @Log(title = "成本汇总", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:summary:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CostSummaryDTO dto) {
        dto.setSummaryId(id);
        costSummaryService.update(dto);
        return Result.success();
    }

    @Log(title = "成本汇总", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:summary:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        costSummaryService.delete(id);
        return Result.success();
    }
}
