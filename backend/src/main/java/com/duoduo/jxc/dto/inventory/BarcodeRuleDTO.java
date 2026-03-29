package com.duoduo.jxc.dto.inventory;

import lombok.Data;

@Data
public class BarcodeRuleDTO {
    private Long ruleId;
    private String ruleName;
    /** 类型: SKU/BOX/BATCH */
    private String ruleType;
    private String prefix;
    private String dateFormat;
    private Integer seqLength;
    private String ruleExpression;
    private String example;
    private Integer isDefault;
    private Integer isActive;
}
