package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.wage.PieceRecordDTO;
import com.duoduo.jxc.dto.wage.PieceRecordQuery;
import com.duoduo.jxc.service.PieceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wage/piece")
@RequiredArgsConstructor
public class PieceRecordController {

    private final PieceRecordService pieceRecordService;

    @Log(title = "计件记录", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('wage:piece:view')")
    public Result<PageResult<PieceRecordDTO>> page(PieceRecordQuery query) {
        return Result.success(pieceRecordService.pageQuery(query));
    }

    @Log(title = "计件记录", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('wage:piece:view')")
    public Result<PieceRecordDTO> getById(@PathVariable Long id) {
        return Result.success(pieceRecordService.getDetail(id));
    }

    @Log(title = "计件记录", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('wage:piece:add')")
    public Result<Long> create(@RequestBody PieceRecordDTO dto) {
        return Result.success(pieceRecordService.create(dto));
    }

    @Log(title = "计件记录", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('wage:piece:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody PieceRecordDTO dto) {
        dto.setId(id);
        pieceRecordService.update(dto);
        return Result.success();
    }

    @Log(title = "计件记录", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('wage:piece:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        pieceRecordService.delete(id);
        return Result.success();
    }

    @Log(title = "计件记录", action = "审核")
    @PutMapping("/{id}/audit")
    @PreAuthorize("@perm.has('wage:piece:audit')")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer auditStatus) {
        pieceRecordService.audit(id, auditStatus);
        return Result.success();
    }

    @Log(title = "计件记录", action = "汇总查询")
    @GetMapping("/summary")
    @PreAuthorize("@perm.has('wage:piece:view')")
    public Result<List<Map<String, Object>>> summary(PieceRecordQuery query) {
        return Result.success(pieceRecordService.summary(query));
    }
}
