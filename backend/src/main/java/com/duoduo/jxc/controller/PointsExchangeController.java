package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.PointsExchangeDTO;
import com.duoduo.jxc.service.PointsExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales/points-exchange")
@RequiredArgsConstructor
public class PointsExchangeController {

    private final PointsExchangeService pointsExchangeService;

    @Log(title = "积分兑换", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('sales:points:view')")
    public Result<PageResult<PointsExchangeDTO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(pointsExchangeService.pageQuery(pageNum, pageSize, keyword, startDate, endDate));
    }

    @Log(title = "积分兑换", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('sales:points:view')")
    public Result<PointsExchangeDTO> getDetail(@PathVariable Long id) {
        return Result.success(pointsExchangeService.getDetail(id));
    }

    @Log(title = "积分兑换", action = "新增兑换记录")
    @PostMapping
    @PreAuthorize("@perm.has('sales:points:add')")
    public Result<Long> create(@RequestBody PointsExchangeDTO dto) {
        return Result.success(pointsExchangeService.create(dto));
    }

    @Log(title = "积分兑换", action = "确认发放")
    @PutMapping("/{id}/confirm")
    @PreAuthorize("@perm.has('sales:points:confirm')")
    public Result<Void> confirm(@PathVariable Long id) {
        pointsExchangeService.confirm(id);
        return Result.success();
    }

    @Log(title = "积分兑换", action = "删除兑换记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('sales:points:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        pointsExchangeService.delete(id);
        return Result.success();
    }
}
