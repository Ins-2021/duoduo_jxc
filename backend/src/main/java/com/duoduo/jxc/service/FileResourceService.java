package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.entity.FileResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileResourceService extends IService<FileResource> {
    FileResource saveFile(MultipartFile file);
    Resource loadAsResource(Long fileId);
}

