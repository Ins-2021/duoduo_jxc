package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.config.FileStorageProperties;
import com.duoduo.jxc.entity.FileResource;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.FileResourceMapper;
import com.duoduo.jxc.service.FileResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileResourceServiceImpl extends ServiceImpl<FileResourceMapper, FileResource> implements FileResourceService {

    private final FileStorageProperties properties;

    @Override
    public FileResource saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(BizCode.FILE_EMPTY);
        }
        long maxBytes = properties.getMaxSizeMb() <= 0 ? 10 * 1024L * 1024L : properties.getMaxSizeMb() * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new BusinessException(BizCode.FILE_SIZE_EXCEEDED);
        }

        String original = file.getOriginalFilename();
        String safeName = sanitizeFileName(StringUtils.hasText(original) ? original : "file");
        String dateDir = LocalDate.now().toString().replace("-", "");
        String storedName = UUID.randomUUID().toString().replace("-", "") + "_" + safeName;

        Path baseDir = Paths.get(properties.getStorageDir()).toAbsolutePath().normalize();
        Path targetDir = baseDir.resolve(dateDir).normalize();
        Path targetPath = targetDir.resolve(storedName).normalize();
        if (!targetPath.startsWith(baseDir)) {
            throw new BusinessException(BizCode.FILE_PATH_INVALID);
        }

        try {
            Files.createDirectories(targetDir);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            try (InputStream in = file.getInputStream()) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    sha.update(buf, 0, len);
                }
            }
            String sha256 = HexFormat.of().formatHex(sha.digest());
            file.transferTo(targetPath);

            FileResource fr = new FileResource();
            fr.setFileName(safeName);
            fr.setContentType(file.getContentType());
            fr.setSize(file.getSize());
            fr.setStoragePath(baseDir.relativize(targetPath).toString().replace("\\", "/"));
            fr.setSha256(sha256);
            save(fr);
            return fr;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(BizCode.FILE_SAVE_FAILED, e);
        }
    }

    @Override
    public Resource loadAsResource(Long fileId) {
        if (fileId == null) {
            throw new BusinessException(BizCode.FILE_ID_EMPTY);
        }
        FileResource fr = getById(fileId);
        if (fr == null) {
            throw new BusinessException(BizCode.FILE_NOT_FOUND);
        }
        Path baseDir = Paths.get(properties.getStorageDir()).toAbsolutePath().normalize();
        Path path = baseDir.resolve(fr.getStoragePath()).normalize();
        if (!path.startsWith(baseDir)) {
            throw new BusinessException(BizCode.FILE_PATH_INVALID);
        }
        Resource resource = new FileSystemResource(path.toFile());
        if (!resource.exists()) {
            throw new BusinessException(BizCode.FILE_NOT_FOUND);
        }
        return resource;
    }

    private static String sanitizeFileName(String name) {
        String n = name.replace("\\", "/");
        if (n.contains("/")) {
            n = n.substring(n.lastIndexOf('/') + 1);
        }
        n = n.replaceAll("[\\r\\n\\t]", "");
        n = n.replaceAll("[^a-zA-Z0-9._\\-()\\u4e00-\\u9fa5]", "_");
        if (!StringUtils.hasText(n)) {
            return "file";
        }
        if (n.length() > 120) {
            return n.substring(n.length() - 120);
        }
        return n;
    }
}

