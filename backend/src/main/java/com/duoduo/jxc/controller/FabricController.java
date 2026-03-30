package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.fabric.FabricDTO;
import com.duoduo.jxc.dto.fabric.FabricQuery;
import com.duoduo.jxc.entity.Fabric;
import com.duoduo.jxc.service.FabricService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fabrics")
@RequiredArgsConstructor
public class FabricController {

    private final FabricService fabricService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<PageResult<FabricDTO>> pageQuery(FabricQuery query) {
        return Result.success(fabricService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<FabricDTO> getDetail(@PathVariable Long id) {
        return Result.success(fabricService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('fabric:add')")
    public Result<Long> create(@RequestBody FabricDTO dto) {
        return Result.success(fabricService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('fabric:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FabricDTO dto) {
        dto.setFabricId(id);
        fabricService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('fabric:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        fabricService.delete(id);
        return Result.success();
    }

    @GetMapping("/list")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<List<FabricDTO>> listAll() {
        return Result.success(fabricService.listAll());
    }
}
