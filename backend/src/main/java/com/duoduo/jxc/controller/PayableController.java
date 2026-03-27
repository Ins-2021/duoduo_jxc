package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.PayableDTO;
import com.duoduo.jxc.service.PayableService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 应付账款Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/finance/payable")
@RequiredArgsConstructor
public class PayableController {

    private final PayableService payableService;

    @Log(title = "应付管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('finance:payable:view')")
    public Result<PageResult<PayableDTO>> page(@RequestBody PageQuery query) {
        return Result.success(payableService.pageList(query));
    }

    @Log(title = "应付管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:payable:view')")
    public Result<PayableDTO> getById(@PathVariable Long id) {
        return Result.success(payableService.getById(id));
    }

    @Log(title = "应付管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('finance:payable:add')")
    public Result<Long> create(@RequestBody PayableDTO dto) {
        return Result.success(payableService.create(dto));
    }

    @Log(title = "应付管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('finance:payable:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody PayableDTO dto) {
        dto.setPayableId(id);
        payableService.update(dto);
        return Result.success();
    }

    @Log(title = "应付管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('finance:payable:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        payableService.delete(id);
        return Result.success();
    }

    @Log(title = "应付管理", action = "核销")
    @PostMapping("/{id}/write-off")
    @PreAuthorize("@perm.has('finance:payable:edit')")
    public Result<Void> writeOff(@PathVariable Long id, @RequestParam java.math.BigDecimal amount) {
        payableService.writeOff(id, amount);
        return Result.success();
    }
}
