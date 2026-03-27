package com.duoduo.jxc.dto.file;

import lombok.Data;

@Data
public class FileUploadResponse {
    private Long fileId;
    private String fileName;
    private String contentType;
    private Long size;
    private String downloadUrl;
}

