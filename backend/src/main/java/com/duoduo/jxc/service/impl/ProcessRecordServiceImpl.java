package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.BundleFlowDTO;
import com.duoduo.jxc.dto.BundleTransferDTO;
import com.duoduo.jxc.dto.ProcessRecordDTO;
import com.duoduo.jxc.dto.ProcessRecordQuery;
import com.duoduo.jxc.dto.ProcessRecordShareDTO;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.entity.BundleProcess;
import com.duoduo.jxc.entity.Process;
import com.duoduo.jxc.entity.ProcessDependency;
import com.duoduo.jxc.entity.ProcessRecord;
import com.duoduo.jxc.entity.ProcessRecordShare;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.mapper.BundleMapper;
import com.duoduo.jxc.mapper.BundleProcessMapper;
import com.duoduo.jxc.mapper.ProcessDependencyMapper;
import com.duoduo.jxc.mapper.ProcessMapper;
import com.duoduo.jxc.mapper.ProcessRecordMapper;
import com.duoduo.jxc.mapper.ProcessRecordShareMapper;
import com.duoduo.jxc.service.ProcessRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessRecordServiceImpl extends ServiceImpl<ProcessRecordMapper, ProcessRecord> implements ProcessRecordService {

    private final BundleMapper bundleMapper;
    private final ProcessMapper processMapper;
    private final ProcessDependencyMapper processDependencyMapper;
    private final BundleProcessMapper bundleProcessMapper;
    private final ProcessRecordShareMapper shareMapper;

    @Override
    public PageResult<ProcessRecordDTO> pageQuery(ProcessRecordQuery query) {
        Page<ProcessRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(query.getWorkerId() != null, ProcessRecord::getWorkerId, query.getWorkerId());
        wrapper.eq(query.getProcessId() != null, ProcessRecord::getProcessId, query.getProcessId());
        wrapper.eq(query.getBundleId() != null, ProcessRecord::getBundleId, query.getBundleId());

        page(page, wrapper);

        List<ProcessRecordDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProcessRecordDTO getDetail(Long id) {
        ProcessRecord entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProcessRecordDTO dto) {
        ProcessRecord entity = new ProcessRecord();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getRecordId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProcessRecordDTO dto) {
        ProcessRecord entity = new ProcessRecord();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessRecordDTO scanRecord(ScanRecordDTO dto) {
        Bundle bundle = bundleMapper.selectByBundleNo(dto.getBundleNo());
        if (bundle == null) {
            throw new BusinessException(BizCode.BUNDLE_NOT_FOUND);
        }

        validateProcessSequence(bundle.getBundleId(), dto.getProcessId());
        checkDuplicateRecord(bundle.getBundleId(), dto.getProcessId(), dto.getWorkerId());

        Process process = processMapper.selectById(dto.getProcessId());
        if (process == null) {
            throw new BusinessException(BizCode.PROCESS_NOT_FOUND);
        }

        ProcessRecord record = new ProcessRecord();
        record.setBundleId(bundle.getBundleId());
        record.setProcessId(dto.getProcessId());
        record.setWorkerId(dto.getWorkerId());
        record.setQuantity(dto.getQuantity());
        record.setAmount(process.getStandardPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        record.setStatus("pending");
        record.setScanTime(LocalDateTime.now());
        save(record);

        updateBundleProcessStatus(bundle, dto.getProcessId(), dto.getWorkerId());

        return convertToDTO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addShares(Long recordId, List<ProcessRecordShareDTO> shares) {
        ProcessRecord record = getById(recordId);
        if (record == null) {
            throw new BusinessException(BizCode.RECORD_NOT_FOUND);
        }

        BigDecimal totalRatio = shares.stream()
            .map(ProcessRecordShareDTO::getShareRatio)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRatio.compareTo(BigDecimal.ONE) != 0) {
            throw new BusinessException(BizCode.INVALID_SHARE_RATIO, "分成比例总和必须为1");
        }

        for (ProcessRecordShareDTO share : shares) {
            ProcessRecordShare entity = new ProcessRecordShare();
            entity.setRecordId(recordId);
            entity.setWorkerId(share.getWorkerId());
            entity.setShareRatio(share.getShareRatio());
            entity.setShareAmount(record.getAmount().multiply(share.getShareRatio()));
            shareMapper.insert(entity);
        }
    }

    @Override
    public BundleFlowDTO getBundleFlow(Long bundleId) {
        Bundle bundle = bundleMapper.selectById(bundleId);
        if (bundle == null) {
            throw new BusinessException(BizCode.BUNDLE_NOT_FOUND);
        }

        BundleFlowDTO dto = new BundleFlowDTO();
        dto.setBundleId(bundle.getBundleId());
        dto.setBundleNo(bundle.getBundleNo());
        dto.setStatus(bundle.getStatus());
        dto.setCreateTime(bundle.getCreateTime());

        List<BundleProcess> bundleProcesses = bundleProcessMapper.selectByBundleId(bundleId);
        List<BundleFlowDTO.BundleProcessFlowDTO> processFlows = bundleProcesses.stream()
            .map(bp -> {
                BundleFlowDTO.BundleProcessFlowDTO flowDTO = new BundleFlowDTO.BundleProcessFlowDTO();
                flowDTO.setProcessId(bp.getProcessId());
                flowDTO.setStatus(bp.getStatus());
                flowDTO.setCompleteTime(bp.getCompleteTime());
                flowDTO.setCompleteWorkerId(bp.getCompleteWorkerId());

                Process process = processMapper.selectById(bp.getProcessId());
                if (process != null) {
                    flowDTO.setProcessCode(process.getProcessCode());
                    flowDTO.setProcessName(process.getProcessName());
                }
                return flowDTO;
            })
            .collect(Collectors.toList());
        dto.setProcesses(processFlows);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferBundle(BundleTransferDTO dto) {
        Bundle bundle = bundleMapper.selectById(dto.getBundleId());
        if (bundle == null) {
            throw new BusinessException(BizCode.BUNDLE_NOT_FOUND);
        }

        BundleProcess bundleProcess = bundleProcessMapper.selectByBundleId(dto.getBundleId()).stream()
            .filter(bp -> bp.getProcessId().equals(dto.getProcessId()))
            .findFirst()
            .orElse(null);

        if (bundleProcess == null) {
            throw new BusinessException(BizCode.BUNDLE_PROCESS_NOT_FOUND);
        }

        bundleProcess.setCompleteWorkerId(dto.getToWorkerId());
        bundleProcessMapper.updateById(bundleProcess);
    }

    private void validateProcessSequence(Long bundleId, Long processId) {
        List<ProcessDependency> dependencies = processDependencyMapper.selectByProcessId(processId);
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }

        List<Long> completedProcesses = baseMapper.selectCompletedProcesses(bundleId);

        for (ProcessDependency dep : dependencies) {
            if (!completedProcesses.contains(dep.getPreProcessId())) {
                Process preProcess = processMapper.selectById(dep.getPreProcessId());
                Process currentProcess = processMapper.selectById(processId);
                String preName = preProcess != null ? preProcess.getProcessName() : "未知工序";
                throw new BusinessException(BizCode.PROCESS_SEQUENCE_ERROR,
                    "工序顺序错误，需要先完成：" + preName);
            }
        }
    }

    private void checkDuplicateRecord(Long bundleId, Long processId, Long workerId) {
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getBundleId, bundleId)
               .eq(ProcessRecord::getProcessId, processId)
               .eq(ProcessRecord::getWorkerId, workerId)
               .in(ProcessRecord::getStatus, "pending", "approved");

        if (count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE_RECORD, "该工序已计件，请勿重复扫码");
        }
    }

    private void updateBundleProcessStatus(Bundle bundle, Long processId, Long workerId) {
        List<BundleProcess> bundleProcesses = bundleProcessMapper.selectByBundleId(bundle.getBundleId());
        if (bundleProcesses == null || bundleProcesses.isEmpty()) {
            return;
        }

        BundleProcess current = bundleProcesses.stream()
            .filter(bp -> bp.getProcessId().equals(processId))
            .findFirst()
            .orElse(null);

        if (current != null) {
            current.setStatus("completed");
            current.setCompleteTime(LocalDateTime.now());
            current.setCompleteWorkerId(workerId);
            bundleProcessMapper.updateById(current);
        }

        boolean allCompleted = bundleProcesses.stream()
            .allMatch(bp -> "completed".equals(bp.getStatus()));

        if (allCompleted) {
            bundle.setStatus("completed");
            bundleMapper.updateById(bundle);
        }
    }

    private ProcessRecordDTO convertToDTO(ProcessRecord entity) {
        ProcessRecordDTO dto = new ProcessRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
