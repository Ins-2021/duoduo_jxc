package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BundleQuery extends PageQuery {
    private String bundleNo;
    private Long orderId;
    private String status;
}
