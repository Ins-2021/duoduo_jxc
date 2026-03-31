package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.dto.BundleFlowDTO;
import com.duoduo.jxc.dto.BundleTransferDTO;
import com.duoduo.jxc.entity.BundleFlow;
import com.duoduo.jxc.mapper.BundleFlowMapper;
import com.duoduo.jxc.service.BundleFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BundleFlowServiceImpl extends ServiceImpl<BundleFlowMapper, BundleFlow> implements BundleFlowService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordCreate(Long bundleId, String bundleNo, Integer quantity) {
        BundleFlow flow = new BundleFlow();
        flow.setBundleId(bundleId);
        flow.setBundleNo(bundleNo);
        flow.setQuantity(quantity);
        flow.setFlowType("CREATE");
        flow.setStatus("COMPLETED");
        flow.setFlowTime(LocalDateTime.now());
        save(flow);
        log.info("bundleFlowCreated bundleId={} bundleNo={}", bundleId, bundleNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordTransfer(BundleTransferDTO dto) {
        BundleFlow flow = new BundleFlow();
        flow.setBundleId(dto.getBundleId());
        flow.setQuantity(1);
        flow.setFlowType("TRANSFER");
        flow.setStatus("COMPLETED");
        flow.setFromWorkerId(dto.getFromWorkerId());
        flow.setToWorkerId(dto.getToWorkerId());
        flow.setRemark(dto.getRemark());
        flow.setFlowTime(LocalDateTime.now());
        save(flow);
        log.info("bundleFlowTransferred bundleId={} from={} to={}",
                dto.getBundleId(), dto.getFromWorkerId(), dto.getToWorkerId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordScan(Long bundleId, String bundleNo, Long processId, String processName,
                           Long workerId, String workerName, Integer quantity) {
        BundleFlow flow = new BundleFlow();
        flow.setBundleId(bundleId);
        flow.setBundleNo(bundleNo);
        flow.setToProcessId(processId);
        flow.setToProcessName(processName);
        flow.setToWorkerId(workerId);
        flow.setToWorkerName(workerName);
        flow.setQuantity(quantity);
        flow.setFlowType("SCAN");
        flow.setStatus("COMPLETED");
        flow.setFlowTime(LocalDateTime.now());
        save(flow);
        log.info("bundleFlowScan bundleId={} processId={} workerId={}", bundleId, processId, workerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordReturn(Long bundleId, String bundleNo, Long fromWorkerId, String fromWorkerName,
                              String reason) {
        BundleFlow flow = new BundleFlow();
        flow.setBundleId(bundleId);
        flow.setBundleNo(bundleNo);
        flow.setFromWorkerId(fromWorkerId);
        flow.setFromWorkerName(fromWorkerName);
        flow.setFlowType("RETURN");
        flow.setStatus("COMPLETED");
        flow.setRemark(reason);
        flow.setFlowTime(LocalDateTime.now());
        save(flow);
        log.info("bundleFlowReturn bundleId={} workerId={} reason={}", bundleId, fromWorkerId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordComplete(Long bundleId, String bundleNo, Integer quantity) {
        BundleFlow flow = new BundleFlow();
        flow.setBundleId(bundleId);
        flow.setBundleNo(bundleNo);
        flow.setQuantity(quantity);
        flow.setFlowType("COMPLETE");
        flow.setStatus("COMPLETED");
        flow.setFlowTime(LocalDateTime.now());
        save(flow);
        log.info("bundleFlowComplete bundleId={} bundleNo={}", bundleId, bundleNo);
    }

    @Override
    public List<BundleFlowDTO> getBundleFlowHistory(Long bundleId) {
        if (bundleId == null) {
            return Collections.emptyList();
        }
        List<BundleFlow> flows = baseMapper.selectByBundleId(bundleId);
        return flows.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public BundleFlowDTO getLatestFlow(Long bundleId) {
        if (bundleId == null) {
            return null;
        }
        BundleFlow flow = baseMapper.selectLatestByBundleId(bundleId);
        return flow != null ? convertToDTO(flow) : null;
    }

    private BundleFlowDTO convertToDTO(BundleFlow entity) {
        BundleFlowDTO dto = new BundleFlowDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
