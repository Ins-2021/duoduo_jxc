package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.dto.ScanRecordQuery;
import com.duoduo.jxc.entity.ScanRecord;

import java.util.List;

public interface ScanService extends IService<ScanRecord> {

    ScanRecordDTO scan(ScanRecordDTO dto);

    ScanRecordDTO scanWithQrCode(String qrCodeNo, Long workerId, Long processId, Integer quantity);

    PageResult<ScanRecordDTO> pageQuery(ScanRecordQuery query);

    List<ScanRecordDTO> getTodayRecordsByWorker(Long workerId);

    ScanRecordDTO getDetail(Long recordId);

    void confirm(Long recordId);

    void reject(Long recordId, String reason);
}
