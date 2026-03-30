package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.fabric.FabricInboundDTO;
import com.duoduo.jxc.dto.fabric.FabricInboundQuery;
import com.duoduo.jxc.service.FabricInboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fabrics/inbounds")
@RequiredArgsConstructor
public class FabricInboundController {

    private final FabricInboundService fabricInboundService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<PageResult<FabricInboundDTO>> pageQuery(FabricInboundQuery query) {
        return Result.success(fabricInboundService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<FabricInboundDTO> getDetail(@PathVariable Long id) {
        return Result.success(fabricInboundService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('fabric:add')")
    public Result<Long> create(@RequestBody FabricInboundDTO dto) {
        return Result.success(fabricInboundService.create(dto));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("@perm.has('fabric:approve')")
    public Result<Void> approve(@PathVariable Long id) {
        fabricInboundService.approve(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('fabric:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        fabricInboundService.delete(id);
        return Result.success();
    }
}
