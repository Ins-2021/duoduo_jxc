package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.wage.PiecePriceDTO;
import com.duoduo.jxc.dto.wage.PiecePriceQuery;
import com.duoduo.jxc.service.PiecePriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wage/price")
@RequiredArgsConstructor
public class PiecePriceController {

    private final PiecePriceService piecePriceService;

    @Log(title = "计件工价", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('wage:price:view')")
    public Result<PageResult<PiecePriceDTO>> page(PiecePriceQuery query) {
        return Result.success(piecePriceService.pageQuery(query));
    }

    @Log(title = "计件工价", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('wage:price:view')")
    public Result<PiecePriceDTO> getById(@PathVariable Long id) {
        return Result.success(piecePriceService.getDetail(id));
    }

    @Log(title = "计件工价", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('wage:price:add')")
    public Result<Long> create(@RequestBody PiecePriceDTO dto) {
        return Result.success(piecePriceService.create(dto));
    }

    @Log(title = "计件工价", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('wage:price:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody PiecePriceDTO dto) {
        dto.setPriceId(id);
        piecePriceService.update(dto);
        return Result.success();
    }

    @Log(title = "计件工价", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('wage:price:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        piecePriceService.delete(id);
        return Result.success();
    }

    /**
     * 计件单价批量设置
     */
    @Log(title = "计件工价", action = "批量设置")
    @PostMapping("/batch")
    @PreAuthorize("@perm.has('wage:price:add')")
    public Result<Void> batchSet(@RequestBody List<PiecePriceDTO> dtoList) {
        for (PiecePriceDTO dto : dtoList) {
            if (dto.getPriceId() != null) {
                piecePriceService.update(dto);
            } else {
                piecePriceService.create(dto);
            }
        }
        return Result.success();
    }
}
