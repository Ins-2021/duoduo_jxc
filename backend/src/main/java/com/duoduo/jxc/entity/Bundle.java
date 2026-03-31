package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    /** 扎包号 */
    private String bundleNo;
    /** 裁床扎包ID */
    private Long cuttingBundleId;
    /** 订单ID */
    private Long orderId;
    /** 尺码 */
    private String size;
    /** 颜色 */
    private String color;
    /** 数量 */
    private Integer quantity;
    /** 当前工序ID */
    private Long currentProcessId;
    /** 负责班组ID */
    private Long workGroupId;
    /** 工作流实例ID */
    @TableField(exist = false)
    private Long wfInstanceId;
    /** 工作流状态 */
    @TableField(exist = false)
    private String wfStatus;
    /** 状态(pending/allocated/producing/completed/abnormal/returned) */
    private String status;
    /** 二维码图片URL */
    private String qrCode;
    /** 二维码内容(JSON) */
    @TableField(exist = false)
    private String qrData;
    /** 当前位置 */
    private String location;
    /** 备注 */
    private String remark;
}
