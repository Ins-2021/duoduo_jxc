package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.BundleFlowDTO;
import com.duoduo.jxc.dto.BundleTransferDTO;
import com.duoduo.jxc.dto.ProcessRecordDTO;
import com.duoduo.jxc.dto.ProcessRecordQuery;
import com.duoduo.jxc.dto.ProcessRecordShareDTO;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.entity.ProcessRecord;

import java.util.List;

public interface ProcessRecordService extends IService<ProcessRecord> {
    PageResult<ProcessRecordDTO> pageQuery(ProcessRecordQuery query);
    ProcessRecordDTO getDetail(Long id);
    Long create(ProcessRecordDTO dto);
    void update(ProcessRecordDTO dto);
    void delete(Long id);

    ProcessRecordDTO scanRecord(ScanRecordDTO dto);
    void addShares(Long recordId, List<ProcessRecordShareDTO> shares);
    BundleFlowDTO getBundleFlow(Long bundleId);
    void transferBundle(BundleTransferDTO dto);
}
