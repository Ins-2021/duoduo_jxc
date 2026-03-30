package com.duoduo.jxc.dto.fabric;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FabricQuery extends PageQuery {
    private String fabricCode;
    private String fabricName;
    private String fabricType;
    private Long supplierId;
    private Integer status;
}
