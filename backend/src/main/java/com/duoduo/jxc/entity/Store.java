package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门店实体类
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_store")
public class Store extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    @TableId(type = IdType.AUTO)
    private Long storeId;

    /**
     * 门店编码
     */
    private String storeCode;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 状态 (1:启用, 0:停用)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
