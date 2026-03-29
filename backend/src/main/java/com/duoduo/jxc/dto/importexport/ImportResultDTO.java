package com.duoduo.jxc.dto.importexport;

import lombok.Data;

@Data
public class ImportResultDTO {

    private Long recordId;

    private Integer totalCount;

    private Integer successCount;

    private Integer errorCount;

    private String errorFilePath;

    public static ImportResultDTO of(Long recordId, Integer total, Integer success, Integer error, String errorFilePath) {
        ImportResultDTO dto = new ImportResultDTO();
        dto.setRecordId(recordId);
        dto.setTotalCount(total);
        dto.setSuccessCount(success);
        dto.setErrorCount(error);
        dto.setErrorFilePath(errorFilePath);
        return dto;
    }
}
