package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.DefectRecordDTO;
import com.duoduo.jxc.dto.QualityCheckDTO;
import com.duoduo.jxc.dto.QualityCheckQuery;
import com.duoduo.jxc.dto.QualityCheckSubmitDTO;
import com.duoduo.jxc.entity.DefectRecord;
import com.duoduo.jxc.entity.QualityCheck;
import com.duoduo.jxc.entity.ReworkOrder;
import com.duoduo.jxc.mapper.DefectRecordMapper;
import com.duoduo.jxc.mapper.QualityCheckMapper;
import com.duoduo.jxc.mapper.ReworkOrderMapper;
import com.duoduo.jxc.service.QualityCheckService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualityCheckServiceImpl extends ServiceImpl<QualityCheckMapper, QualityCheck> implements QualityCheckService {

    private final DefectRecordMapper defectRecordMapper;
    private final ReworkOrderMapper reworkOrderMapper;

    public QualityCheckServiceImpl(DefectRecordMapper defectRecordMapper, ReworkOrderMapper reworkOrderMapper) {
        this.defectRecordMapper = defectRecordMapper;
        this.reworkOrderMapper = reworkOrderMapper;
    }

    @Override
    public PageResult<QualityCheckDTO> pageQuery(QualityCheckQuery query) {
        Page<QualityCheck> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<QualityCheck> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getCheckNo()), QualityCheck::getCheckNo, query.getCheckNo())
               .eq(query.getBundleId() != null, QualityCheck::getBundleId, query.getBundleId())
               .eq(query.getProcessId() != null, QualityCheck::getProcessId, query.getProcessId())
               .eq(StringUtils.hasText(query.getResult()), QualityCheck::getResult, query.getResult())
               .eq(query.getInspectorId() != null, QualityCheck::getInspectorId, query.getInspectorId())
               .orderByDesc(QualityCheck::getCreateTime);

        page(page, wrapper);

        List<QualityCheckDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public QualityCheckDTO getDetail(Long id) {
        QualityCheck entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(QualityCheckDTO dto) {
        QualityCheck entity = new QualityCheck();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getCheckId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QualityCheckDTO dto) {
        QualityCheck entity = new QualityCheck();
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
    public Long submitCheckResult(QualityCheckSubmitDTO dto) {
        QualityCheck check = new QualityCheck();
        BeanUtils.copyProperties(dto, check);
        save(check);
        
        if ("rejected".equals(dto.getResult()) && dto.getDefects() != null && !dto.getDefects().isEmpty()) {
            createReworkOrder(check, dto.getDefects());
        }
        
        return check.getCheckId();
    }

    private void createReworkOrder(QualityCheck check, List<DefectRecordDTO> defects) {
        ReworkOrder rework = new ReworkOrder();
        rework.setCheckId(check.getCheckId());
        rework.setBundleId(check.getBundleId());
        rework.setStatus("pending");
        reworkOrderMapper.insert(rework);
        
        for (DefectRecordDTO defect : defects) {
            DefectRecord record = new DefectRecord();
            BeanUtils.copyProperties(defect, record);
            record.setReworkId(rework.getReworkId());
            defectRecordMapper.insert(record);
        }
    }

    private QualityCheckDTO convertToDTO(QualityCheck entity) {
        QualityCheckDTO dto = new QualityCheckDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
