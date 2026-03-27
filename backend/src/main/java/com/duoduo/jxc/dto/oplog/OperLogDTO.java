package com.duoduo.jxc.dto.oplog;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperLogDTO {
    private Long logId;
    private String storeName;
    private LocalDateTime createTime;
    private String content;
    private String operatorName;
}

