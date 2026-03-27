package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_receipt")
public class Receipt {
    @TableId(type = IdType.AUTO)
    private Long receiptId;
    private String receiptNo;
    private Long customerId;
    private String customerName;
    private Long accountId;
    private String accountName;
    private BigDecimal amount;
    private String payMethod;
    private Integer status;
    private LocalDateTime receiptDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
}
