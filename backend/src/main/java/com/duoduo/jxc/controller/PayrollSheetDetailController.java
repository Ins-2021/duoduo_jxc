package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.wage.PayrollSheetDetailDTO;
import com.duoduo.jxc.dto.wage.PayrollSheetDetailQuery;
import com.duoduo.jxc.service.PayrollSheetDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wage/sheet-detail")
@RequiredArgsConstructor
public class PayrollSheetDetailController {

    private final PayrollSheetDetailService payrollSheetDetailService;

    @Log(title = "工资单明细", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('wage:sheet:view')")
    public Result<PageResult<PayrollSheetDetailDTO>> page(PayrollSheetDetailQuery query) {
        return Result.success(payrollSheetDetailService.pageQuery(query));
    }

    @Log(title = "工资单明细", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('wage:sheet:view')")
    public Result<PayrollSheetDetailDTO> getById(@PathVariable Long id) {
        return Result.success(payrollSheetDetailService.getDetail(id));
    }

    @Log(title = "工资单明细", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('wage:sheet:add')")
    public Result<Long> create(@RequestBody PayrollSheetDetailDTO dto) {
        return Result.success(payrollSheetDetailService.create(dto));
    }

    @Log(title = "工资单明细", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('wage:sheet:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody PayrollSheetDetailDTO dto) {
        dto.setId(id);
        payrollSheetDetailService.update(dto);
        return Result.success();
    }

    @Log(title = "工资单明细", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('wage:sheet:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        payrollSheetDetailService.delete(id);
        return Result.success();
    }
}
