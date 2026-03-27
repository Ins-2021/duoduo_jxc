package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_file_resource")
public class FileResource extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long fileId;

    private String fileName;

    private String contentType;

    private Long size;

    private String storagePath;

    private String sha256;
}

