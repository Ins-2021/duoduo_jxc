package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.importexport.ImportResultDTO;
import com.duoduo.jxc.entity.ImportExportRecord;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImportExportService {

    void downloadTemplate(String bizType, HttpServletResponse response);

    ImportResultDTO importData(MultipartFile file, String bizType) throws Exception;

    void exportData(String bizType, Map<String, Object> params, HttpServletResponse response);

    ImportExportRecord getImportRecord(Long recordId);
}
