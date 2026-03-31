package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.PayableDTO;
import com.duoduo.jxc.entity.Payable;
import com.duoduo.jxc.mapper.PayableMapper;
import com.duoduo.jxc.service.PayableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应付账款Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
public class PayableServiceImpl extends ServiceImpl<PayableMapper, Payable> implements PayableService {

    private final FinanceConverter converter;
    private final com.duoduo.jxc.mapper.WriteOffMapper writeOffMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<PayableDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<Payable> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Payable::getBillNo, keyword)
                    .or().like(Payable::getSupplierName, keyword));
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(Payable::getStatus, status);
        }
        wrapper.orderByDesc(Payable::getCreateTime);

        IPage<Payable> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<PayableDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PayableDTO getById(Long id) {
        Payable entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PayableDTO dto) {
        Payable entity = new Payable();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setBillNo(generateBillNo());
        entity.setStatus(0);
        entity.setPaidAmount(BigDecimal.ZERO);
        entity.setRemainingAmount(entity.getAmount());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        return entity.getPayableId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PayableDTO dto) {
        Payable entity = new Payable();
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
    public void writeOff(Long id, BigDecimal amount) {
        Payable entity = super.getById(id);
        if (entity == null) {
            throw new com.duoduo.jxc.exception.BusinessException("应付账款不存在");
        }
        if (entity.getRemainingAmount().compareTo(amount) < 0) {
            throw new com.duoduo.jxc.exception.BusinessException("核销金额不能大于剩余金额");
        }
        
        entity.setPaidAmount(entity.getPaidAmount().add(amount));
        entity.setRemainingAmount(entity.getRemainingAmount().subtract(amount));
        
        if (entity.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            entity.setStatus(2); // 已核销
        } else {
            entity.setStatus(1); // 部分核销
        }
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);

        // 生成核销记录到 jxc_write_off 表
        com.duoduo.jxc.entity.WriteOff writeOff = new com.duoduo.jxc.entity.WriteOff();
        writeOff.setWriteOffNo("HX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase());
        writeOff.setType("PAYABLE");
        writeOff.setBillNo(entity.getBillNo());
        writeOff.setBillId(entity.getPayableId());
        writeOff.setAmount(amount);
        writeOff.setCreateTime(LocalDateTime.now());
        writeOffMapper.insert(writeOff);
    }

    private PayableDTO toDTO(Payable entity) {
        PayableDTO dto = new PayableDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateBillNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "YF" + dateStr + randomStr;
    }
}
