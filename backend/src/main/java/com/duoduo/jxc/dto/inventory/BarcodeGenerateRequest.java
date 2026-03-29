package com.duoduo.jxc.dto.inventory;

import lombok.Data;

@Data
public class BarcodeGenerateRequest {
    /** 条码类型: SKU/BOX/BATCH */
    private String barcodeType;
    /** 关联类型: SKU/BATCH/BOX */
    private String refType;
    /** 关联ID */
    private Long refId;
    /** 生成数量(默认1) */
    private Integer count;
    /** 规则ID(可选, 不传则使用默认规则) */
    private Long ruleId;
}
