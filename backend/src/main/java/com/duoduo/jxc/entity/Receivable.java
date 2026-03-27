package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_receivable")
public class Receivable {
    @TableId(type = IdType.AUTO)
    private Long receivableId;
    private String billNo;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private BigDecimal receivedAmount;
    private BigDecimal remainingAmount;
    private Integer status;
    private LocalDateTime billDate;
    private LocalDateTime dueDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
