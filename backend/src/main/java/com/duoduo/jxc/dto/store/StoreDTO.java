package com.duoduo.jxc.dto.store;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 门店数据传输对象
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Data
public class StoreDTO {

    private Long storeId;

    @NotBlank(message = "门店编码不能为空")
    private String storeCode;

    @NotBlank(message = "门店名称不能为空")
    private String storeName;

    private String address;

    private String phone;

    private String contactName;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
