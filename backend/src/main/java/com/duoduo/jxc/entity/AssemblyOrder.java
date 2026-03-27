package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_assembly_order")
public class AssemblyOrder {
    @TableId(type = IdType.AUTO)
    private Long assemblyId;
    private String assemblyNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer type;
    private Integer status;
    private LocalDateTime assemblyDate;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    private Long updatedBy;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
