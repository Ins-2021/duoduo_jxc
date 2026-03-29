package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_supplier")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Long supplierId;
    private String supplierName;
    private String contactName;
    private String contactPhone;
    private String address;
    private BigDecimal initialArrears;
    /** 评级: A/B/C/D */
    private String rating;
    /** 银行账户 */
    private String bankAccount;
    /** 开户行 */
    private String bankName;
    /** 结算方式 */
    private String settlementType;
    /** 信用额度 */
    private BigDecimal creditLimit;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

