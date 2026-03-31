package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.importexport.ImportResultDTO;
import com.duoduo.jxc.entity.ImportExportRecord;
import com.duoduo.jxc.service.ImportExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/import-export")
@RequiredArgsConstructor
public class ImportExportController {

    private final ImportExportService importExportService;

    @GetMapping("/template/{bizType}")
    @PreAuthorize("@perm.has('system:import:template')")
    public void downloadTemplate(@PathVariable String bizType, HttpServletResponse response) {
        importExportService.downloadTemplate(bizType, response);
    }

    @PostMapping("/import/{bizType}")
    @PreAuthorize("@perm.has('system:import:data')")
    public Result<ImportResultDTO> importData(@PathVariable String bizType, @RequestParam("file") MultipartFile file) {
        try {
            ImportResultDTO result = importExportService.importData(file, bizType);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/export/{bizType}")
    @PreAuthorize("@perm.has('system:export:data')")
    public void exportData(@PathVariable String bizType, @RequestBody(required = false) Map<String, Object> params, HttpServletResponse response) {
        importExportService.exportData(bizType, params != null ? params : Map.of(), response);
    }

    @GetMapping("/record/{recordId}")
    @PreAuthorize("@perm.has('system:import:view')")
    public Result<ImportExportRecord> getImportRecord(@PathVariable Long recordId) {
        return Result.success(importExportService.getImportRecord(recordId));
    }
}
