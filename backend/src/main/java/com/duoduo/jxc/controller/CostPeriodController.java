package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.CostPeriodDTO;
import com.duoduo.jxc.dto.cost.CostPeriodQuery;
import com.duoduo.jxc.service.CostPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/period")
@RequiredArgsConstructor
public class CostPeriodController {

    private final CostPeriodService costPeriodService;

    @Log(title = "成本核算期间", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:period:view')")
    public Result<PageResult<CostPeriodDTO>> page(CostPeriodQuery query) {
        return Result.success(costPeriodService.pageQuery(query));
    }

    @Log(title = "成本核算期间", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:period:view')")
    public Result<CostPeriodDTO> getById(@PathVariable Long id) {
        return Result.success(costPeriodService.getDetail(id));
    }

    @Log(title = "成本核算期间", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('cost:period:add')")
    public Result<Long> create(@RequestBody CostPeriodDTO dto) {
        return Result.success(costPeriodService.create(dto));
    }

    @Log(title = "成本核算期间", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:period:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CostPeriodDTO dto) {
        dto.setPeriodId(id);
        costPeriodService.update(dto);
        return Result.success();
    }

    @Log(title = "成本核算期间", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:period:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        costPeriodService.delete(id);
        return Result.success();
    }

    @Log(title = "成本核算期间", action = "更新状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('cost:period:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        costPeriodService.updateStatus(id, status);
        return Result.success();
    }
}
