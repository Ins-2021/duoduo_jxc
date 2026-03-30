package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工序异常表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_process_exception")
public class ProcessException extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 异常编号 */
    private String exceptionNo;
    /** 扎包ID */
    private Long bundleId;
    /** 工序ID */
    private Long processId;
    /** 工人ID */
    private Long workerId;
    /** 异常类型(defect/material/skip/machine) */
    private String exceptionType;
    /** 异常描述 */
    private String description;
    /** 处理方式(rework/scrap/pass) */
    private String handlingMethod;
    /** 处理结果说明 */
    private String handlingResult;
    /** 处理人ID */
    private Long handledBy;
    /** 处理时间 */
    private java.time.LocalDateTime handledTime;
    /** 状态(open/handling/resolved) */
    private String status;
}
