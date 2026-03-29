package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.CostPoolDTO;
import com.duoduo.jxc.dto.cost.CostPoolQuery;
import com.duoduo.jxc.service.CostPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/pool")
@RequiredArgsConstructor
public class CostPoolController {

    private final CostPoolService costPoolService;

    @Log(title = "成本池", action = "分页查询成本池")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:pool:view')")
    public Result<PageResult<CostPoolDTO>> page(CostPoolQuery query) {
        return Result.success(costPoolService.pageQuery(query));
    }

    @Log(title = "成本池", action = "查询成本池详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:pool:view')")
    public Result<CostPoolDTO> getById(@PathVariable Long id) {
        return Result.success(costPoolService.getDetail(id));
    }

    @Log(title = "成本池", action = "创建成本池")
    @PostMapping
    @PreAuthorize("@perm.has('cost:pool:add')")
    public Result<Long> create(@RequestBody CostPoolDTO dto) {
        return Result.success(costPoolService.create(dto));
    }

    @Log(title = "成本池", action = "修改成本池")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:pool:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CostPoolDTO dto) {
        dto.setPoolId(id);
        costPoolService.update(dto);
        return Result.success();
    }

    @Log(title = "成本池", action = "删除成本池")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:pool:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        costPoolService.delete(id);
        return Result.success();
    }
}
