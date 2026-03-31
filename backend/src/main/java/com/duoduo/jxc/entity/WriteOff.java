package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_write_off")
public class WriteOff {
    @TableId(type = IdType.AUTO)
    private Long writeOffId;
    private String writeOffNo;
    private String type;
    private String billNo;
    private Long billId;
    private Long receiptPaymentId;
    private BigDecimal amount;
    private String remark;
    private Long createdBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
