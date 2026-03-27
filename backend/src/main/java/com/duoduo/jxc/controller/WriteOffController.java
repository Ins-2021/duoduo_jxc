package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.finance.WriteOffDTO;
import com.duoduo.jxc.service.WriteOffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 核销单Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/finance/write-off")
@RequiredArgsConstructor
public class WriteOffController {

    private final WriteOffService writeOffService;

    @Log(title = "核销管理", action = "分页查询")
    @PostMapping("/page")
    @PreAuthorize("@perm.has('finance:write-off:view')")
    public Result<PageResult<WriteOffDTO>> page(@RequestBody PageQuery query) {
        return Result.success(writeOffService.pageList(query));
    }

    @Log(title = "核销管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('finance:write-off:view')")
    public Result<WriteOffDTO> getById(@PathVariable Long id) {
        return Result.success(writeOffService.getById(id));
    }

    @Log(title = "核销管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('finance:write-off:add')")
    public Result<Long> create(@RequestBody WriteOffDTO dto) {
        return Result.success(writeOffService.create(dto));
    }

    @Log(title = "核销管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('finance:write-off:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        writeOffService.delete(id);
        return Result.success();
    }
}
