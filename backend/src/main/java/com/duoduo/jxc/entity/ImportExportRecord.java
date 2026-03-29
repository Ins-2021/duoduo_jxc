package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_import_export_record")
public class ImportExportRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long recordId;

    private String bizType;

    private String operation;

    private String fileName;

    private String filePath;

    private Integer status;

    private Integer totalCount;

    private Integer successCount;

    private Integer errorCount;

    private String errorFilePath;
}
