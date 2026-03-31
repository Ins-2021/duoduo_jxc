package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_scan_record")
public class ScanRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long recordId;

    private Long bundleId;

    private String bundleNo;

    private Long processId;

    private String processName;

    private Long workerId;

    private String workerName;

    private Long workGroupId;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal amount;

    private String scanType;

    private String qrContent;

    private String deviceInfo;

    private String ipAddress;

    private String status;

    private LocalDateTime scanAt;

    private LocalDateTime confirmAt;
}
