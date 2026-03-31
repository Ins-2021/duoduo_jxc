package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.fabric.FabricRequisitionDTO;
import com.duoduo.jxc.dto.fabric.FabricRequisitionQuery;
import com.duoduo.jxc.service.FabricRequisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fabrics/requisitions")
@RequiredArgsConstructor
public class FabricRequisitionController {

    private final FabricRequisitionService fabricRequisitionService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<PageResult<FabricRequisitionDTO>> pageQuery(FabricRequisitionQuery query) {
        return Result.success(fabricRequisitionService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('fabric:view')")
    public Result<FabricRequisitionDTO> getDetail(@PathVariable Long id) {
        return Result.success(fabricRequisitionService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('fabric:add')")
    public Result<Long> create(@RequestBody FabricRequisitionDTO dto) {
        return Result.success(fabricRequisitionService.create(dto));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("@perm.has('fabric:approve')")
    public Result<Void> approve(@PathVariable Long id, @RequestParam(required = false) String comment, @RequestParam boolean approved) {
        fabricRequisitionService.approve(id, comment, approved);
        return Result.success();
    }

    @PostMapping("/{id}/issue")
    @PreAuthorize("@perm.has('fabric:issue')")
    public Result<Void> issue(@PathVariable Long id) {
        fabricRequisitionService.issue(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('fabric:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        fabricRequisitionService.delete(id);
        return Result.success();
    }
}
