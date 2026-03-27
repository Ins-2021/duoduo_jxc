package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_finance_transaction")
public class FinanceTransaction {
    @TableId(type = IdType.AUTO)
    private Long transactionId;
    private String transactionNo;
    private Long accountId;
    private String accountName;
    private Integer type;
    private BigDecimal amount;
    private BigDecimal balance;
    private String category;
    private String billType;
    private String billNo;
    private String remark;
    private LocalDateTime transactionDate;
    private LocalDateTime createTime;
}
