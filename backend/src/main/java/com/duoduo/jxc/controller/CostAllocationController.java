package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.CostAllocationDTO;
import com.duoduo.jxc.dto.cost.CostAllocationQuery;
import com.duoduo.jxc.service.CostAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/allocation")
@RequiredArgsConstructor
public class CostAllocationController {

    private final CostAllocationService costAllocationService;

    @Log(title = "成本分摊", action = "分页查询成本分摊")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:allocation:view')")
    public Result<PageResult<CostAllocationDTO>> page(CostAllocationQuery query) {
        return Result.success(costAllocationService.pageQuery(query));
    }

    @Log(title = "成本分摊", action = "查询成本分摊详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:allocation:view')")
    public Result<CostAllocationDTO> getById(@PathVariable Long id) {
        return Result.success(costAllocationService.getDetail(id));
    }

    @Log(title = "成本分摊", action = "创建成本分摊")
    @PostMapping
    @PreAuthorize("@perm.has('cost:allocation:add')")
    public Result<Long> create(@RequestBody CostAllocationDTO dto) {
        return Result.success(costAllocationService.create(dto));
    }

    @Log(title = "成本分摊", action = "修改成本分摊")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:allocation:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CostAllocationDTO dto) {
        dto.setAllocationId(id);
        costAllocationService.update(dto);
        return Result.success();
    }

    @Log(title = "成本分摊", action = "删除成本分摊")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:allocation:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        costAllocationService.delete(id);
        return Result.success();
    }
}
