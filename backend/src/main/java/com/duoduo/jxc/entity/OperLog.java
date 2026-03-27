package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_oper_log")
public class OperLog extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long logId;

    private Long storeId;
    private String storeName;

    private Long operatorId;
    private String operatorName;

    private String title;
    private String action;
    private String content;

    private String requestMethod;
    private String requestUrl;
    private String requestIp;

    private String requestParams;
    private String responseData;

    private Integer status;
    private String errorMsg;
    private Long costTime;
}

