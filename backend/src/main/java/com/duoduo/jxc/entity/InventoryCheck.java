package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_inventory_check")
public class InventoryCheck {
    @TableId(type = IdType.AUTO)
    private Long checkId;
    private String checkNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private LocalDateTime checkDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    private Long updatedBy;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
