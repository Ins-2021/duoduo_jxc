package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_stock_in_out")
public class StockInOut {
    @TableId(type = IdType.AUTO)
    private Long inOutId;
    private String billNo;
    private Integer type;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private LocalDateTime billDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    private Long updatedBy;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
