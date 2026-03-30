package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bundle")
public class Bundle extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long bundleId;
    private String bundleNo;
    private Long orderId;
    private Long currentProcessId;
    private String status;
    private java.time.LocalDateTime completeTime;
}
