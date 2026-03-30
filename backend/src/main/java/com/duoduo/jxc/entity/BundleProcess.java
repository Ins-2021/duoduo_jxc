package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bundle_process")
public class BundleProcess extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long bpId;
    private Long bundleId;
    private Long processId;
    private String status;
    private LocalDateTime completeTime;
    private Long completeWorkerId;
}
