package com.duoduo.jxc.enums;

import lombok.Getter;

@Getter
public enum FabricRequisitionStatusEnum implements DictEnum {
    DRAFT(0, "待审批"),
    APPROVED(1, "已审批"),
    ISSUED(2, "已领料"),
    REJECTED(3, "已拒绝");

    private final int code;
    private final String label;

    FabricRequisitionStatusEnum(int code, String label) {
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
