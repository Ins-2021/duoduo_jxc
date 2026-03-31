package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.ReceivableDTO;
import com.duoduo.jxc.entity.Receivable;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.entity.FinanceTransaction;
import com.duoduo.jxc.enums.TransactionTypeEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.ReceivableMapper;
import com.duoduo.jxc.service.FinanceTransactionService;
import com.duoduo.jxc.service.ReceivableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 应收账款Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReceivableServiceImpl extends ServiceImpl<ReceivableMapper, Receivable> implements ReceivableService {

    private final FinanceConverter converter;
    private final FinanceTransactionService financeTransactionService;
    private final com.duoduo.jxc.mapper.WriteOffMapper writeOffMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<ReceivableDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<Receivable> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Receivable::getBillNo, keyword)
                    .or().like(Receivable::getCustomerName, keyword));
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(Receivable::getStatus, status);
        }
        wrapper.orderByDesc(Receivable::getCreateTime);

        IPage<Receivable> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<ReceivableDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ReceivableDTO getById(Long id) {
        Receivable entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ReceivableDTO dto) {
        Receivable entity = new Receivable();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setBillNo(generateBillNo());
        entity.setStatus(0);
        entity.setReceivedAmount(BigDecimal.ZERO);
        entity.setRemainingAmount(entity.getAmount());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        return entity.getReceivableId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReceivableDTO dto) {
        Receivable entity = new Receivable();
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
        Receivable entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.RECEIVABLE_NOT_FOUND);
        }
        if (entity.getRemainingAmount().compareTo(amount) < 0) {
            throw new BusinessException(BizCode.RECEIVABLE_AMOUNT_MISMATCH);
        }
        
        entity.setReceivedAmount(entity.getReceivedAmount().add(amount));
        entity.setRemainingAmount(entity.getRemainingAmount().subtract(amount));
        
        if (entity.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            entity.setStatus(2); // 已核销
        } else {
            entity.setStatus(1); // 部分核销
        }
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);

        // ========== 新增：生成核销记录到 jxc_write_off 表 ==========
        com.duoduo.jxc.entity.WriteOff writeOff = new com.duoduo.jxc.entity.WriteOff();
        writeOff.setWriteOffNo("HX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase());
        writeOff.setType("RECEIVABLE");
        writeOff.setBillNo(entity.getBillNo());
        writeOff.setBillId(entity.getReceivableId());
        writeOff.setAmount(amount);
        writeOff.setCreateTime(LocalDateTime.now());
        writeOffMapper.insert(writeOff);

        // ========== 新增：生成资金流水 ==========
        createFinanceTransaction(entity, amount);
    }

    /**
     * 生成资金流水记录
     */
    private void createFinanceTransaction(Receivable receivable, BigDecimal amount) {
        FinanceTransaction trans = new FinanceTransaction();
        trans.setTransactionNo("JZ" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase());
        trans.setAccountId(null);
        trans.setAccountName("默认账户");
        trans.setType(TransactionTypeEnum.INCOME.getCode()); // 1-收入
        trans.setAmount(amount);
        trans.setBalance(null); // 余额后续计算
        trans.setCategory("RECEIVABLE_WRITE_OFF");
        trans.setBillType("RECEIVABLE");
        trans.setBillNo(receivable.getBillNo());
        trans.setRemark("应收核销：" + receivable.getBillNo());
        trans.setTransactionDate(LocalDateTime.now());
        trans.setCreateTime(LocalDateTime.now());

        financeTransactionService.create(trans);
        log.info("收款核销生成资金流水: receivableId={}, amount={}, transactionNo={}",
                receivable.getReceivableId(), amount, trans.getTransactionNo());
    }

    private ReceivableDTO toDTO(Receivable entity) {
        ReceivableDTO dto = new ReceivableDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateBillNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "YS" + dateStr + randomStr;
    }
}
