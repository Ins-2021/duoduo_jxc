package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.StandardCostDTO;
import com.duoduo.jxc.dto.cost.StandardCostQuery;
import com.duoduo.jxc.service.StandardCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/standard")
@RequiredArgsConstructor
public class StandardCostController {

    private final StandardCostService standardCostService;

    @Log(title = "标准成本", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:standard:view')")
    public Result<PageResult<StandardCostDTO>> page(StandardCostQuery query) {
        return Result.success(standardCostService.pageQuery(query));
    }

    @Log(title = "标准成本", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:standard:view')")
    public Result<StandardCostDTO> getById(@PathVariable Long id) {
        return Result.success(standardCostService.getDetail(id));
    }

    @Log(title = "标准成本", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('cost:standard:add')")
    public Result<Long> create(@RequestBody StandardCostDTO dto) {
        return Result.success(standardCostService.create(dto));
    }

    @Log(title = "标准成本", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:standard:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody StandardCostDTO dto) {
        dto.setCostId(id);
        standardCostService.update(dto);
        return Result.success();
    }

    @Log(title = "标准成本", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:standard:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        standardCostService.delete(id);
        return Result.success();
    }
}
