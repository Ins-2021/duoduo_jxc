package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.data.CustomerDTO;
import com.duoduo.jxc.dto.data.CustomerQuery;
import com.duoduo.jxc.entity.Customer;
import com.duoduo.jxc.service.data.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:customer:view')")
    public Result<PageResult<Customer>> pageQuery(CustomerQuery query) {
        return Result.success(customerService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:customer:view')")
    public Result<Customer> getDetail(@PathVariable Long id) {
        return Result.success(customerService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('data:customer:add')")
    public Result<Long> create(@RequestBody CustomerDTO dto) {
        return Result.success(customerService.create(dto));
    }

    @PutMapping
    @PreAuthorize("@perm.has('data:customer:edit')")
    public Result<Void> update(@RequestBody CustomerDTO dto) {
        customerService.update(dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('data:customer:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody CustomerDTO dto) {
        customerService.updateStatus(id, dto.getStatus());
        return Result.success();
    }
}
