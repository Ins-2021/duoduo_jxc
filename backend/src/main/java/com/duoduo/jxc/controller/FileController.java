package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.file.FileUploadResponse;
import com.duoduo.jxc.entity.FileResource;
import com.duoduo.jxc.service.FileResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileResourceService fileResourceService;

    @Log(title = "文件", action = "上传文件")
    @PostMapping("/upload")
    @PreAuthorize("@perm.has('common:file:upload')")
    public Result<FileUploadResponse> upload(@RequestPart("file") MultipartFile file) {
        FileResource saved = fileResourceService.saveFile(file);
        FileUploadResponse resp = new FileUploadResponse();
        resp.setFileId(saved.getFileId());
        resp.setFileName(saved.getFileName());
        resp.setContentType(saved.getContentType());
        resp.setSize(saved.getSize());
        resp.setDownloadUrl("/api/files/" + saved.getFileId() + "/download");
        return Result.success(resp);
    }

    @Log(title = "文件", action = "获取文件元数据")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('common:file:download')")
    public Result<FileUploadResponse> meta(@PathVariable Long id) {
        FileResource fr = fileResourceService.getById(id);
        if (fr == null) {
            return Result.error(404, "文件不存在");
        }
        FileUploadResponse resp = new FileUploadResponse();
        resp.setFileId(fr.getFileId());
        resp.setFileName(fr.getFileName());
        resp.setContentType(fr.getContentType());
        resp.setSize(fr.getSize());
        resp.setDownloadUrl("/api/files/" + fr.getFileId() + "/download");
        return Result.success(resp);
    }

    @Log(title = "文件", action = "下载文件")
    @GetMapping("/{id}/download")
    @PreAuthorize("@perm.has('common:file:download')")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        FileResource fr = fileResourceService.getById(id);
        if (fr == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = fileResourceService.loadAsResource(id);
        String filename = fr.getFileName();
        String contentType = fr.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fr.getContentType();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
