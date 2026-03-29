package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.cost.CostCenterDTO;
import com.duoduo.jxc.dto.cost.CostCenterQuery;
import com.duoduo.jxc.service.CostCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost/center")
@RequiredArgsConstructor
public class CostCenterController {

    private final CostCenterService costCenterService;

    @Log(title = "成本中心", action = "分页查询成本中心")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('cost:center:view')")
    public Result<PageResult<CostCenterDTO>> page(CostCenterQuery query) {
        return Result.success(costCenterService.pageQuery(query));
    }

    @Log(title = "成本中心", action = "查询成本中心详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('cost:center:view')")
    public Result<CostCenterDTO> getById(@PathVariable Long id) {
        return Result.success(costCenterService.getDetail(id));
    }

    @Log(title = "成本中心", action = "创建成本中心")
    @PostMapping
    @PreAuthorize("@perm.has('cost:center:add')")
    public Result<Long> create(@RequestBody CostCenterDTO dto) {
        return Result.success(costCenterService.create(dto));
    }

    @Log(title = "成本中心", action = "修改成本中心")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('cost:center:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody CostCenterDTO dto) {
        dto.setCostCenterId(id);
        costCenterService.update(dto);
        return Result.success();
    }

    @Log(title = "成本中心", action = "删除成本中心")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('cost:center:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        costCenterService.delete(id);
        return Result.success();
    }
}
