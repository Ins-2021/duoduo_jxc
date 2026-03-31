package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.CustomerRechargeDTO;
import com.duoduo.jxc.service.CustomerRechargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales/customer-recharge")
@RequiredArgsConstructor
public class CustomerRechargeController {

    private final CustomerRechargeService customerRechargeService;

    @Log(title = "客户充值", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('sales:recharge:view')")
    public Result<PageResult<CustomerRechargeDTO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(customerRechargeService.pageQuery(pageNum, pageSize, keyword, startDate, endDate));
    }

    @Log(title = "客户充值", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('sales:recharge:view')")
    public Result<CustomerRechargeDTO> getDetail(@PathVariable Long id) {
        return Result.success(customerRechargeService.getDetail(id));
    }

    @Log(title = "客户充值", action = "新增充值")
    @PostMapping
    @PreAuthorize("@perm.has('sales:recharge:add')")
    public Result<Long> recharge(@RequestBody CustomerRechargeDTO dto) {
        return Result.success(customerRechargeService.recharge(dto));
    }
}
