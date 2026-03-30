package com.duoduo.jxc.dto.fabric;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FabricInboundQuery extends PageQuery {
    private String inboundNo;
    private Long fabricId;
    private Long warehouseId;
    private Long supplierId;
    private Integer status;
}
