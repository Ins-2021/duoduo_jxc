package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_income_expense")
public class IncomeExpense {
    @TableId(type = IdType.AUTO)
    private Long ieId;
    private String ieNo;
    private Integer type;
    private Long categoryId;
    private String categoryName;
    private Long accountId;
    private String accountName;
    private BigDecimal amount;
    private String billType;
    private String billNo;
    private Integer status;
    private LocalDateTime billDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
