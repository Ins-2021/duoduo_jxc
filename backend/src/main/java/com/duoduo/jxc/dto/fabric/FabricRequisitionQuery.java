package com.duoduo.jxc.dto.fabric;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FabricRequisitionQuery extends PageQuery {
    private String requisitionNo;
    private Long fabricId;
    private Long warehouseId;
    private Long applicantId;
    private Integer status;
}
