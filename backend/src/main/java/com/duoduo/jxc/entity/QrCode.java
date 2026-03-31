package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_qr_code")
public class QrCode extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long qrId;

    private String qrCodeNo;

    private String refType;

    private Long refId;

    private String qrContent;

    private String qrImageUrl;

    private String signature;

    private LocalDateTime expireAt;

    private Integer printed;

    private Integer printCount;

    private LocalDateTime lastPrintAt;

    private Integer scanCount;

    private LocalDateTime lastScanAt;

    private String status;
}
