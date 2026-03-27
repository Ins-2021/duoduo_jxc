package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_customer")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long customerId;
    private String customerName;
    private String contactName;
    private String contactPhone;
    private String address;
    private String level;
    private BigDecimal initialArrears;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

