package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.bom.BomDTO;
import com.duoduo.jxc.dto.bom.BomItemDTO;
import com.duoduo.jxc.dto.bom.BomProcessDTO;
import com.duoduo.jxc.dto.bom.BomQuery;
import com.duoduo.jxc.service.BomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bom")
@RequiredArgsConstructor
public class BomController {

    private final BomService bomService;

    @Log(title = "BOM管理", action = "分页查询")
    @PostMapping("/list")
    @PreAuthorize("@perm.has('bom:view')")
    public Result<PageResult<BomDTO>> list(@RequestBody BomQuery query) {
        return Result.success(bomService.pageQuery(query));
    }

    @Log(title = "BOM管理", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('bom:view')")
    public Result<BomDTO> getById(@PathVariable Long id) {
        return Result.success(bomService.getDetail(id));
    }

    @Log(title = "BOM管理", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('bom:add')")
    public Result<Long> create(@RequestBody BomDTO dto) {
        return Result.success(bomService.create(dto));
    }

    @Log(title = "BOM管理", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('bom:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody BomDTO dto) {
        dto.setBomId(id);
        bomService.update(dto);
        return Result.success();
    }

    @Log(title = "BOM管理", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('bom:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        bomService.delete(id);
        return Result.success();
    }

    @Log(title = "BOM管理", action = "获取BOM明细")
    @GetMapping("/{id}/items")
    @PreAuthorize("@perm.has('bom:view')")
    public Result<List<BomItemDTO>> getItems(@PathVariable Long id) {
        return Result.success(bomService.getItems(id));
    }

    @Log(title = "BOM管理", action = "添加BOM明细")
    @PostMapping("/{id}/items")
    @PreAuthorize("@perm.has('bom:edit')")
    public Result<Void> saveItems(@PathVariable Long id, @RequestBody List<BomItemDTO> items) {
        bomService.saveItems(id, items);
        return Result.success();
    }

    @Log(title = "BOM管理", action = "获取BOM工序")
    @GetMapping("/{id}/processes")
    @PreAuthorize("@perm.has('bom:view')")
    public Result<List<BomProcessDTO>> getProcesses(@PathVariable Long id) {
        return Result.success(bomService.getProcesses(id));
    }

    @Log(title = "BOM管理", action = "添加BOM工序")
    @PostMapping("/{id}/processes")
    @PreAuthorize("@perm.has('bom:edit')")
    public Result<Void> saveProcesses(@PathVariable Long id, @RequestBody List<BomProcessDTO> processes) {
        bomService.saveProcesses(id, processes);
        return Result.success();
    }

    @Log(title = "BOM管理", action = "按款式查询")
    @GetMapping("/style/{styleId}")
    @PreAuthorize("@perm.has('bom:view')")
    public Result<List<BomDTO>> listByStyleId(@PathVariable Long styleId) {
        return Result.success(bomService.listByStyleId(styleId));
    }
}
