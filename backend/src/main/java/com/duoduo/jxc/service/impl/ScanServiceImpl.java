package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.constant.QrCodeConstant;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.dto.ScanRecordQuery;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.entity.Process;
import com.duoduo.jxc.entity.QrCode;
import com.duoduo.jxc.entity.ScanRecord;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.BundleMapper;
import com.duoduo.jxc.mapper.ProcessMapper;
import com.duoduo.jxc.mapper.ScanRecordMapper;
import com.duoduo.jxc.service.BundleFlowService;
import com.duoduo.jxc.service.DuplicateCheckService;
import com.duoduo.jxc.service.QrCodeService;
import com.duoduo.jxc.service.ScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl extends ServiceImpl<ScanRecordMapper, ScanRecord> implements ScanService {

    private final BundleMapper bundleMapper;
    private final ProcessMapper processMapper;
    private final QrCodeService qrCodeService;
    private final DuplicateCheckService duplicateCheckService;
    private final BundleFlowService bundleFlowService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScanRecordDTO scan(ScanRecordDTO dto) {
        Bundle bundle = bundleMapper.selectByBundleNo(dto.getBundleNo());
        if (bundle == null) {
            throw new BusinessException(BizCode.BUNDLE_NOT_FOUND);
        }

        Process process = processMapper.selectById(dto.getProcessId());
        if (process == null) {
            throw new BusinessException(BizCode.PROCESS_NOT_FOUND);
        }

        validateScanQuantity(dto.getQuantity());

        boolean lockAcquired = false;
        try {
            lockAcquired = duplicateCheckService.tryAcquireScanLock(
                    dto.getBundleNo(), dto.getWorkerId(), dto.getProcessId());
            if (!lockAcquired) {
                throw new BusinessException(BizCode.DUPLICATE_RECORD, "正在处理中，请稍后重试");
            }

            if (duplicateCheckService.isScannedToday(bundle.getBundleId(), dto.getWorkerId(), dto.getProcessId())) {
                throw new BusinessException(BizCode.DUPLICATE_RECORD, "今日已扫码计件，请勿重复操作");
            }

            ScanRecord record = createScanRecord(bundle, process, dto);
            save(record);

            duplicateCheckService.markScanned(bundle.getBundleId(), dto.getWorkerId(), dto.getProcessId());

            bundleFlowService.recordScan(bundle.getBundleId(), bundle.getBundleNo(),
                    process.getProcessId(), process.getProcessName(),
                    dto.getWorkerId(), null, dto.getQuantity());

            log.info("scanSuccess recordId={} bundleNo={} processId={} workerId={}",
                    record.getRecordId(), dto.getBundleNo(), dto.getProcessId(), dto.getWorkerId());

            return convertToDTO(record);
        } finally {
            if (lockAcquired) {
                duplicateCheckService.releaseScanLock(dto.getBundleNo(), dto.getWorkerId(), dto.getProcessId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScanRecordDTO scanWithQrCode(String qrCodeNo, Long workerId, Long processId, Integer quantity) {
        QrCode qrCode = qrCodeService.getByQrCodeNo(qrCodeNo);
        if (qrCode == null) {
            throw new BusinessException(BizCode.NOT_FOUND, "二维码不存在");
        }

        if (!"ACTIVE".equals(qrCode.getStatus())) {
            throw new BusinessException(BizCode.NOT_FOUND, "二维码已失效");
        }

        if (qrCode.getExpireAt() != null && qrCode.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BizCode.NOT_FOUND, "二维码已过期");
        }

        if (!QrCodeConstant.QR_TYPE_BUNDLE.equals(qrCode.getRefType())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "非扎包二维码");
        }

        Bundle bundle = bundleMapper.selectById(qrCode.getRefId());
        if (bundle == null) {
            throw new BusinessException(BizCode.BUNDLE_NOT_FOUND);
        }

        qrCodeService.incrementScanCount(qrCode.getQrId());

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundle.getBundleNo());
        dto.setProcessId(processId);
        dto.setWorkerId(workerId);
        dto.setQuantity(quantity != null ? quantity : QrCodeConstant.MIN_PIECE_QUANTITY);

        return scan(dto);
    }

    @Override
    public PageResult<ScanRecordDTO> pageQuery(ScanRecordQuery query) {
        Page<ScanRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ScanRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(query.getWorkerId() != null, ScanRecord::getWorkerId, query.getWorkerId());
        wrapper.eq(query.getProcessId() != null, ScanRecord::getProcessId, query.getProcessId());
        wrapper.eq(query.getBundleId() != null, ScanRecord::getBundleId, query.getBundleId());
        wrapper.like(query.getBundleNo() != null && !query.getBundleNo().isEmpty(),
                ScanRecord::getBundleNo, query.getBundleNo());
        wrapper.eq(query.getStatus() != null && !query.getStatus().isEmpty(),
                ScanRecord::getStatus, query.getStatus());
        wrapper.ge(query.getStartTime() != null, ScanRecord::getScanAt, query.getStartTime());
        wrapper.le(query.getEndTime() != null, ScanRecord::getScanAt, query.getEndTime());
        wrapper.orderByDesc(ScanRecord::getScanAt);

        page(page, wrapper);

        List<ScanRecordDTO> dtoList = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public List<ScanRecordDTO> getTodayRecordsByWorker(Long workerId) {
        if (workerId == null) {
            return Collections.emptyList();
        }
        List<ScanRecord> records = baseMapper.selectByWorkerAndDate(workerId, LocalDateTime.now());
        return records.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public ScanRecordDTO getDetail(Long recordId) {
        ScanRecord record = getById(recordId);
        return record != null ? convertToDTO(record) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long recordId) {
        ScanRecord record = getById(recordId);
        if (record == null) {
            throw new BusinessException(BizCode.NOT_FOUND, "扫码记录不存在");
        }
        record.setStatus("CONFIRMED");
        record.setConfirmAt(LocalDateTime.now());
        updateById(record);
        log.info("scanRecordConfirmed recordId={}", recordId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long recordId, String reason) {
        ScanRecord record = getById(recordId);
        if (record == null) {
            throw new BusinessException(BizCode.NOT_FOUND, "扫码记录不存在");
        }
        record.setStatus("REJECTED");
        updateById(record);
        log.info("scanRecordRejected recordId={} reason={}", recordId, reason);
    }

    private void validateScanQuantity(Integer quantity) {
        if (quantity == null || quantity < QrCodeConstant.MIN_PIECE_QUANTITY) {
            throw new BusinessException(BizCode.BAD_REQUEST,
                    "数量不能小于" + QrCodeConstant.MIN_PIECE_QUANTITY);
        }
        if (quantity > QrCodeConstant.MAX_PIECE_QUANTITY) {
            throw new BusinessException(BizCode.BAD_REQUEST,
                    "数量不能大于" + QrCodeConstant.MAX_PIECE_QUANTITY);
        }
    }

    private ScanRecord createScanRecord(Bundle bundle, Process process, ScanRecordDTO dto) {
        ScanRecord record = new ScanRecord();
        record.setBundleId(bundle.getBundleId());
        record.setBundleNo(bundle.getBundleNo());
        record.setProcessId(process.getProcessId());
        record.setProcessName(process.getProcessName());
        record.setWorkerId(dto.getWorkerId());
        record.setQuantity(dto.getQuantity());
        record.setPrice(process.getStandardPrice());
        record.setAmount(process.getStandardPrice() != null
                ? process.getStandardPrice().multiply(BigDecimal.valueOf(dto.getQuantity()))
                : BigDecimal.ZERO);
        record.setScanType("QR_CODE");
        record.setStatus("CONFIRMED");
        record.setScanAt(LocalDateTime.now());
        return record;
    }

    private ScanRecordDTO convertToDTO(ScanRecord entity) {
        ScanRecordDTO dto = new ScanRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
