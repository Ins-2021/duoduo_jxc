package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.BundleFlowDTO;
import com.duoduo.jxc.dto.BundleTransferDTO;
import com.duoduo.jxc.entity.BundleFlow;

import java.util.List;

public interface BundleFlowService extends IService<BundleFlow> {

    void recordCreate(Long bundleId, String bundleNo, Integer quantity);

    void recordTransfer(BundleTransferDTO dto);

    void recordScan(Long bundleId, String bundleNo, Long processId, String processName,
                    Long workerId, String workerName, Integer quantity);

    void recordReturn(Long bundleId, String bundleNo, Long fromWorkerId, String fromWorkerName,
                      String reason);

    void recordComplete(Long bundleId, String bundleNo, Integer quantity);

    List<BundleFlowDTO> getBundleFlowHistory(Long bundleId);

    BundleFlowDTO getLatestFlow(Long bundleId);
}
