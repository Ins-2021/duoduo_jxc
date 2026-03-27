package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.oplog.OperLogDTO;
import com.duoduo.jxc.dto.oplog.OperLogQuery;
import com.duoduo.jxc.entity.OperLog;

public interface OperLogService extends IService<OperLog> {
    PageResult<OperLogDTO> pageQuery(OperLogQuery query);

    void clearAll();
}
