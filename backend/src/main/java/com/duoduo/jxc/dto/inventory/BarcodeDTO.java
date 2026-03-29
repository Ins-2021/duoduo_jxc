package com.duoduo.jxc.dto.inventory;

import lombok.Data;

@Data
public class BarcodeDTO {
    private Long barcodeId;
    private String barcodeContent;
    private String refType;
    private Long refId;
    private Long ruleId;
    private String barcodeType;
    private Integer printed;
}
