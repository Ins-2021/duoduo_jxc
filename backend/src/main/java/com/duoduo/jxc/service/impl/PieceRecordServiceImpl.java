package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PieceRecordDTO;
import com.duoduo.jxc.dto.wage.PieceRecordQuery;
import com.duoduo.jxc.entity.PieceRecord;
import com.duoduo.jxc.mapper.PieceRecordMapper;
import com.duoduo.jxc.service.PieceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PieceRecordServiceImpl extends ServiceImpl<PieceRecordMapper, PieceRecord> implements PieceRecordService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<PieceRecordDTO> pageQuery(PieceRecordQuery query) {
        Page<PieceRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PieceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRecordNo()), PieceRecord::getRecordNo, query.getRecordNo())
               .ge(query.getRecordDateFrom() != null, PieceRecord::getRecordDate, query.getRecordDateFrom())
               .le(query.getRecordDateTo() != null, PieceRecord::getRecordDate, query.getRecordDateTo())
               .eq(query.getProductionId() != null, PieceRecord::getProductionId, query.getProductionId())
               .eq(query.getStyleId() != null, PieceRecord::getStyleId, query.getStyleId())
               .eq(query.getEmployeeId() != null, PieceRecord::getEmployeeId, query.getEmployeeId())
               .like(StringUtils.hasText(query.getEmployeeName()), PieceRecord::getEmployeeName, query.getEmployeeName())
               .eq(StringUtils.hasText(query.getProcessCode()), PieceRecord::getProcessCode, query.getProcessCode())
               .eq(query.getWorkshopId() != null, PieceRecord::getWorkshopId, query.getWorkshopId())
               .eq(query.getAuditStatus() != null, PieceRecord::getAuditStatus, query.getAuditStatus())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(PieceRecord::getRecordNo, query.getKeyword())
                    .or().like(PieceRecord::getEmployeeName, query.getKeyword())
                    .or().like(PieceRecord::getStyleName, query.getKeyword()))
               .orderByDesc(PieceRecord::getCreateTime);
        page(page, wrapper);
        List<PieceRecordDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PieceRecordDTO getDetail(Long id) {
        PieceRecord entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PieceRecordDTO dto) {
        PieceRecord entity = new PieceRecord();
        BeanUtils.copyProperties(dto, entity);
        entity.setRecordNo(generateRecordNo());
        if (entity.getAuditStatus() == null) {
            entity.setAuditStatus(0);
        }
        if (entity.getQuantity() == null) {
            entity.setQuantity(BigDecimal.ZERO);
        }
        if (entity.getQualifiedQuantity() == null) {
            entity.setQualifiedQuantity(BigDecimal.ZERO);
        }
        if (entity.getDefectQuantity() == null) {
            entity.setDefectQuantity(BigDecimal.ZERO);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PieceRecordDTO dto) {
        PieceRecord entity = new PieceRecord();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, Integer auditStatus) {
        PieceRecord entity = getById(id);
        if (entity == null) {
            throw new RuntimeException("计件记录不存在");
        }
        if (entity.getAuditStatus() != 0) {
            throw new RuntimeException("只有待审核状态的记录才能审核");
        }
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2)) {
            throw new RuntimeException("审核状态无效，应为1(通过)或2(驳回)");
        }
        entity.setAuditStatus(auditStatus);
        entity.setAuditTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    public List<Map<String, Object>> summary(PieceRecordQuery query) {
        LambdaQueryWrapper<PieceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(query.getRecordDateFrom() != null, PieceRecord::getRecordDate, query.getRecordDateFrom())
               .le(query.getRecordDateTo() != null, PieceRecord::getRecordDate, query.getRecordDateTo())
               .eq(query.getEmployeeId() != null, PieceRecord::getEmployeeId, query.getEmployeeId())
               .eq(query.getProductionId() != null, PieceRecord::getProductionId, query.getProductionId())
               .eq(query.getStyleId() != null, PieceRecord::getStyleId, query.getStyleId())
               .eq(StringUtils.hasText(query.getProcessCode()), PieceRecord::getProcessCode, query.getProcessCode())
               .eq(query.getWorkshopId() != null, PieceRecord::getWorkshopId, query.getWorkshopId());
        List<PieceRecord> records = list(wrapper);

        Map<String, Map<String, Object>> summaryMap = new LinkedHashMap<>();
        for (PieceRecord record : records) {
            String key = record.getEmployeeId() + "_" + record.getStyleId() + "_" + record.getProcessCode();
            Map<String, Object> item = summaryMap.computeIfAbsent(key, k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("employeeId", record.getEmployeeId());
                m.put("employeeCode", record.getEmployeeCode());
                m.put("employeeName", record.getEmployeeName());
                m.put("styleId", record.getStyleId());
                m.put("styleCode", record.getStyleCode());
                m.put("styleName", record.getStyleName());
                m.put("processCode", record.getProcessCode());
                m.put("processName", record.getProcessName());
                m.put("totalQuantity", BigDecimal.ZERO);
                m.put("totalQualifiedQuantity", BigDecimal.ZERO);
                m.put("totalDefectQuantity", BigDecimal.ZERO);
                m.put("totalWageAmount", BigDecimal.ZERO);
                m.put("recordCount", 0);
                return m;
            });
            item.put("totalQuantity", ((BigDecimal) item.get("totalQuantity")).add(
                    record.getQuantity() != null ? record.getQuantity() : BigDecimal.ZERO));
            item.put("totalQualifiedQuantity", ((BigDecimal) item.get("totalQualifiedQuantity")).add(
                    record.getQualifiedQuantity() != null ? record.getQualifiedQuantity() : BigDecimal.ZERO));
            item.put("totalDefectQuantity", ((BigDecimal) item.get("totalDefectQuantity")).add(
                    record.getDefectQuantity() != null ? record.getDefectQuantity() : BigDecimal.ZERO));
            item.put("totalWageAmount", ((BigDecimal) item.get("totalWageAmount")).add(
                    record.getWageAmount() != null ? record.getWageAmount() : BigDecimal.ZERO));
            item.put("recordCount", (int) item.get("recordCount") + 1);
        }
        return new ArrayList<>(summaryMap.values());
    }

    private PieceRecordDTO toDTO(PieceRecord entity) {
        PieceRecordDTO dto = new PieceRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateRecordNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PR" + dateStr + randomStr;
    }
}
