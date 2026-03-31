package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PayrollSheetDTO;
import com.duoduo.jxc.dto.wage.PayrollSheetQuery;
import com.duoduo.jxc.entity.wage.PayrollSheet;
import com.duoduo.jxc.enums.SalarySheetStatusEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PayrollSheetMapper;
import com.duoduo.jxc.service.PayrollSheetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollSheetServiceImpl extends ServiceImpl<PayrollSheetMapper, PayrollSheet> implements PayrollSheetService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<PayrollSheetDTO> pageQuery(PayrollSheetQuery query) {
        Page<PayrollSheet> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PayrollSheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getSheetNo()), PayrollSheet::getSheetNo, query.getSheetNo())
               .eq(StringUtils.hasText(query.getYearMonth()), PayrollSheet::getYearMonth, query.getYearMonth())
               .eq(query.getDepartmentId() != null, PayrollSheet::getDepartmentId, query.getDepartmentId())
               .eq(query.getStatus() != null, PayrollSheet::getStatus, query.getStatus())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(PayrollSheet::getSheetNo, query.getKeyword()))
               .orderByDesc(PayrollSheet::getCreateTime);
        page(page, wrapper);
        List<PayrollSheetDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PayrollSheetDTO getDetail(Long id) {
        PayrollSheet entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PayrollSheetDTO dto) {
        PayrollSheet entity = new PayrollSheet();
        BeanUtils.copyProperties(dto, entity);
        entity.setSheetNo(generateSheetNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getEmployeeCount() == null) {
            entity.setEmployeeCount(0);
        }
        save(entity);
        return entity.getSheetId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PayrollSheetDTO dto) {
        PayrollSheet entity = new PayrollSheet();
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
    public void updateStatus(Long id, Integer status) {
        PayrollSheet entity = new PayrollSheet();
        entity.setSheetId(id);
        entity.setStatus(status);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long id) {
        PayrollSheet entity = getById(id);
        if (entity == null) {
            throw new BusinessException("工资单不存在");
        }
        if (entity.getStatus() != SalarySheetStatusEnum.DRAFT.getCode()) {
            throw new BusinessException("只有草稿状态的工资单才能提交");
        }
        PayrollSheet update = new PayrollSheet();
        update.setSheetId(id);
        update.setStatus(SalarySheetStatusEnum.PENDING_AUDIT.getCode());
        update.setSubmitTime(LocalDateTime.now());
        updateById(update);
        log.info("工资单[{}]已提交审核", entity.getSheetNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, Integer approved) {
        PayrollSheet entity = getById(id);
        if (entity == null) {
            throw new BusinessException("工资单不存在");
        }
        if (entity.getStatus() != SalarySheetStatusEnum.PENDING_AUDIT.getCode()) {
            throw new BusinessException("只有待审核状态的工资单才能审核");
        }
        PayrollSheet update = new PayrollSheet();
        update.setSheetId(id);
        if (approved == SalarySheetStatusEnum.APPROVED.getCode()) {
            update.setStatus(SalarySheetStatusEnum.APPROVED.getCode());
            update.setAuditTime(LocalDateTime.now());
            log.info("工资单[{}]审核通过", entity.getSheetNo());
        } else {
            update.setStatus(SalarySheetStatusEnum.DRAFT.getCode());
            log.info("工资单[{}]审核驳回", entity.getSheetNo());
        }
        updateById(update);
    }

    private PayrollSheetDTO toDTO(PayrollSheet entity) {
        PayrollSheetDTO dto = new PayrollSheetDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateSheetNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PS" + dateStr + randomStr;
    }
}
