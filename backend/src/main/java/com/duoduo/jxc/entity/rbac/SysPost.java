package com.duoduo.jxc.entity.rbac;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sys_post")
public class SysPost extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long postId;
    private String postCode;
    private String postName;
    private Integer postSort;
    private Integer status;
    private String remark;
}