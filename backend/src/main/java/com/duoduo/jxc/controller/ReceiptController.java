package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.ReceiptDTO;
import com.duoduo.jxc.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 收款单Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/finance/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @Log(title = "收款管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('finance:receipt:view')")
    public Result<PageResult<ReceiptDTO>> page(@RequestBody PageQuery query) {
        return Result.success(receiptService.pageList(query));
    }

    @Log(title = "收款管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:receipt:view')")
    public Result<ReceiptDTO> getById(@PathVariable Long id) {
        return Result.success(receiptService.getById(id));
    }

    @Log(title = "收款管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('finance:receipt:add')")
    public Result<Long> create(@RequestBody ReceiptDTO dto) {
        return Result.success(receiptService.create(dto));
    }

    @Log(title = "收款管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('finance:receipt:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReceiptDTO dto) {
        dto.setReceiptId(id);
        receiptService.update(dto);
        return Result.success();
    }

    @Log(title = "收款管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('finance:receipt:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        receiptService.delete(id);
        return Result.success();
    }

    @Log(title = "收款管理", action = "完成收款")
    @PostMapping("/{id}/complete")
    @PreAuthorize("@perm.has('finance:receipt:complete')")
    public Result<Void> complete(@PathVariable Long id) {
        receiptService.complete(id);
        return Result.success();
    }
}
