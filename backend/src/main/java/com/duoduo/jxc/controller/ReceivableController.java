package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.ReceivableDTO;
import com.duoduo.jxc.service.ReceivableService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 应收账款Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/finance/receivable")
@RequiredArgsConstructor
public class ReceivableController {

    private final ReceivableService receivableService;

    @Log(title = "应收管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('finance:receivable:view')")
    public Result<PageResult<ReceivableDTO>> page(@RequestBody PageQuery query) {
        return Result.success(receivableService.pageList(query));
    }

    @Log(title = "应收管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:receivable:view')")
    public Result<ReceivableDTO> getById(@PathVariable Long id) {
        return Result.success(receivableService.getById(id));
    }

    @Log(title = "应收管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('finance:receivable:add')")
    public Result<Long> create(@RequestBody ReceivableDTO dto) {
        return Result.success(receivableService.create(dto));
    }

    @Log(title = "应收管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('finance:receivable:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReceivableDTO dto) {
        dto.setReceivableId(id);
        receivableService.update(dto);
        return Result.success();
    }

    @Log(title = "应收管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('finance:receivable:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        receivableService.delete(id);
        return Result.success();
    }

    @Log(title = "应收管理", action = "核销")
    @PostMapping("/{id}/write-off")
    @PreAuthorize("@perm.has('finance:receivable:edit')")
    public Result<Void> writeOff(@PathVariable Long id, @RequestParam java.math.BigDecimal amount) {
        receivableService.writeOff(id, amount);
        return Result.success();
    }
}
