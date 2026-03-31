package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_first_article_confirmation")
public class FirstArticleConfirmation extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long confirmationId;
    private String confirmationNo;
    private Long orderId;
    private Long processId;
    private String status;
    private String result;
    private Long checkerId;
    private Long approverId;
    private String approveComment;
    private java.time.LocalDateTime approveTime;
    private java.time.LocalDateTime checkTime;
    private String remark;

    /** 覆盖BaseEntity的deleted字段，数据库中不存在该列 */
    @TableField(exist = false)
    private Integer deleted;
}
