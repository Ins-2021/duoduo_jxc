package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.common.OptionDTO;
import com.duoduo.jxc.service.OptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class OptionsController {
    private final OptionsService optionsService;

    @Log(title = "下拉选项", action = "查询门店下拉")
    @GetMapping("/stores")
    @PreAuthorize("@perm.has('system:store:view')")
    public Result<List<OptionDTO>> stores() {
        return Result.success(optionsService.listStores());
    }

    @Log(title = "下拉选项", action = "查询客户下拉")
    @GetMapping("/customers")
    @PreAuthorize("@perm.has('data:customer:view')")
    public Result<List<OptionDTO>> customers() {
        return Result.success(optionsService.listCustomers());
    }

    @Log(title = "下拉选项", action = "查询供应商下拉")
    @GetMapping("/suppliers")
    @PreAuthorize("@perm.has('data:supplier:view')")
    public Result<List<OptionDTO>> suppliers() {
        return Result.success(optionsService.listSuppliers());
    }

    @Log(title = "下拉选项", action = "查询仓库下拉")
    @GetMapping("/warehouses")
    @PreAuthorize("@perm.has('data:warehouse:view')")
    public Result<List<OptionDTO>> warehouses() {
        return Result.success(optionsService.listWarehouses());
    }

    @Log(title = "下拉选项", action = "查询职员下拉")
    @GetMapping("/staff")
    @PreAuthorize("@perm.has('system:user:view')")
    public Result<List<OptionDTO>> staff() {
        return Result.success(optionsService.listStaff());
    }

    @Log(title = "下拉选项", action = "查询资金账户下拉")
    @GetMapping("/finance-accounts")
    @PreAuthorize("@perm.has('finance:account:view')")
    public Result<List<OptionDTO>> financeAccounts() {
        return Result.success(optionsService.listFinanceAccounts());
    }
}


