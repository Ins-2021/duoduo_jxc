package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_defect_record")
public class DefectRecord extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long defectId;
    private String defectNo;
    private Long qualityCheckId;
    private Long reworkId;
    private String defectType;
    private Integer quantity;
    private String handlingMethod;
    private String handlingRemark;
    private Long handledBy;
    private java.time.LocalDateTime handledAt;
}
