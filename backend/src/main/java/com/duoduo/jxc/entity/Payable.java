package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_payable")
public class Payable {
    @TableId(type = IdType.AUTO)
    private Long payableId;
    private String billNo;
    private Long supplierId;
    private String supplierName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private Integer status;
    private LocalDateTime billDate;
    private LocalDateTime dueDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
