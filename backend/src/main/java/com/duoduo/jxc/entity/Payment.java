package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long paymentId;
    private String paymentNo;
    private Long supplierId;
    private String supplierName;
    private Long accountId;
    private String accountName;
    private BigDecimal amount;
    private String payMethod;
    private Integer status;
    private LocalDateTime paymentDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
