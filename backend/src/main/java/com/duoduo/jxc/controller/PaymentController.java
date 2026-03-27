package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.PaymentDTO;
import com.duoduo.jxc.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 付款单Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/finance/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Log(title = "付款管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('finance:payment:view')")
    public Result<PageResult<PaymentDTO>> page(@RequestBody PageQuery query) {
        return Result.success(paymentService.pageList(query));
    }

    @Log(title = "付款管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:payment:view')")
    public Result<PaymentDTO> getById(@PathVariable Long id) {
        return Result.success(paymentService.getById(id));
    }

    @Log(title = "付款管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('finance:payment:add')")
    public Result<Long> create(@RequestBody PaymentDTO dto) {
        return Result.success(paymentService.create(dto));
    }

    @Log(title = "付款管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('finance:payment:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody PaymentDTO dto) {
        dto.setPaymentId(id);
        paymentService.update(dto);
        return Result.success();
    }

    @Log(title = "付款管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('finance:payment:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return Result.success();
    }

    @Log(title = "付款管理", action = "完成付款")
    @PostMapping("/{id}/complete")
    @PreAuthorize("@perm.has('finance:payment:complete')")
    public Result<Void> complete(@PathVariable Long id) {
        paymentService.complete(id);
        return Result.success();
    }
}
