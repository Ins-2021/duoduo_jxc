package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("jxc_points_exchange")
public class PointsExchange {
    @TableId(type = IdType.AUTO)
    private Long exchangeId;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private Integer points;
    private Integer quantity;
    private Integer status;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime exchangeTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
