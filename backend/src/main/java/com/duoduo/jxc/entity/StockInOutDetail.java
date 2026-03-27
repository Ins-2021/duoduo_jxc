package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_stock_in_out_detail")
public class StockInOutDetail {
    @TableId(type = IdType.AUTO)
    private Long detailId;
    private Long inOutId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String attr1;
    private String attr2;
    private Integer qty;
    private BigDecimal costPrice;
    private BigDecimal amount;
}
