package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum FabricInboundStatusEnum implements DictEnum {
    DRAFT(0, "待审核"),
    APPROVED(1, "已审核");

    private final int code;
    private final String label;

    FabricInboundStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
