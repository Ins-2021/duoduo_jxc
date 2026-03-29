package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PayrollPeriodDTO;
import com.duoduo.jxc.dto.wage.PayrollPeriodQuery;
import com.duoduo.jxc.entity.PayrollPeriod;
import com.duoduo.jxc.mapper.PayrollPeriodMapper;
import com.duoduo.jxc.service.PayrollPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollPeriodServiceImpl extends ServiceImpl<PayrollPeriodMapper, PayrollPeriod> implements PayrollPeriodService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<PayrollPeriodDTO> pageQuery(PayrollPeriodQuery query) {
        Page<PayrollPeriod> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PayrollPeriod> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getSheetNo()), PayrollPeriod::getSheetNo, query.getSheetNo())
               .eq(StringUtils.hasText(query.getYearMonth()), PayrollPeriod::getYearMonth, query.getYearMonth())
               .eq(query.getStatus() != null, PayrollPeriod::getStatus, query.getStatus())
               .ge(query.getStartDateFrom() != null, PayrollPeriod::getStartDate, query.getStartDateFrom())
               .le(query.getStartDateTo() != null, PayrollPeriod::getStartDate, query.getStartDateTo())
               .ge(query.getEndDateFrom() != null, PayrollPeriod::getEndDate, query.getEndDateFrom())
               .le(query.getEndDateTo() != null, PayrollPeriod::getEndDate, query.getEndDateTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(PayrollPeriod::getSheetNo, query.getKeyword())
                    .or().like(PayrollPeriod::getYearMonth, query.getKeyword()))
               .orderByDesc(PayrollPeriod::getCreateTime);
        page(page, wrapper);
        List<PayrollPeriodDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PayrollPeriodDTO getDetail(Long id) {
        PayrollPeriod entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PayrollPeriodDTO dto) {
        PayrollPeriod entity = new PayrollPeriod();
        BeanUtils.copyProperties(dto, entity);
        entity.setSheetNo(generateSheetNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PayrollPeriodDTO dto) {
        PayrollPeriod entity = new PayrollPeriod();
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
    public void updateStatus(Long id, Integer status) {
        PayrollPeriod entity = new PayrollPeriod();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long id) {
        PayrollPeriod entity = getById(id);
        if (entity == null || entity.getStatus() != 0) {
            throw new RuntimeException("只有草稿状态的工资单才能提交审核");
        }
        entity.setStatus(1);
        entity.setSubmitTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, boolean approved) {
        PayrollPeriod entity = getById(id);
        if (entity == null || entity.getStatus() != 1) {
            throw new RuntimeException("只有待审核状态的工资单才能审核");
        }
        entity.setStatus(approved ? 2 : 0);
        entity.setAuditTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private PayrollPeriodDTO toDTO(PayrollPeriod entity) {
        PayrollPeriodDTO dto = new PayrollPeriodDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateSheetNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "WS" + dateStr + randomStr;
    }
}
