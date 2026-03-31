package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.RetailReturnDTO;
import com.duoduo.jxc.entity.RetailReturn;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.RetailReturnMapper;
import com.duoduo.jxc.service.RetailReturnService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetailReturnServiceImpl extends ServiceImpl<RetailReturnMapper, RetailReturn> implements RetailReturnService {

    @Override
    public PageResult<RetailReturnDTO> pageQuery(int pageNum, int pageSize, String keyword, Integer status, String startDate, String endDate) {
        Page<RetailReturn> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<RetailReturn> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(RetailReturn::getDocNo, keyword)
                    .or().like(RetailReturn::getCustomerName, keyword));
        }
        if (status != null) {
            wrapper.eq(RetailReturn::getStatus, status);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(RetailReturn::getReturnDate, LocalDate.parse(startDate));
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(RetailReturn::getReturnDate, LocalDate.parse(endDate));
        }
        wrapper.orderByDesc(RetailReturn::getCreateTime);
        
        page(page, wrapper);
        
        List<RetailReturnDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(RetailReturnDTO dto) {
        RetailReturn entity = new RetailReturn();
        BeanUtils.copyProperties(dto, entity);
        entity.setDocNo(generateDocNo());
        entity.setStatus(0);
        entity.setTotalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : java.math.BigDecimal.ZERO);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);
        save(entity);
        log.info("创建零售退货单: docNo={}", entity.getDocNo());
        return entity.getReturnId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long returnId, Long auditBy, String auditByName) {
        RetailReturn entity = getById(returnId);
        if (entity == null) {
            throw new BusinessException(BizCode.NOT_FOUND, "退货单不存在");
        }
        if (entity.getStatus() != 0) {
            throw new BusinessException(BizCode.BAD_REQUEST, "该退货单已审核");
        }
        entity.setStatus(1);
        entity.setAuditBy(auditBy);
        entity.setAuditByName(auditByName);
        entity.setAuditTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
        log.info("审核零售退货单: returnId={}", returnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long returnId) {
        RetailReturn entity = getById(returnId);
        if (entity == null) {
            return;
        }
        if (entity.getStatus() != 0) {
            throw new BusinessException(BizCode.BAD_REQUEST, "已审核的退货单不能删除");
        }
        removeById(returnId);
        log.info("删除零售退货单: returnId={}", returnId);
    }

    @Override
    public RetailReturnDTO getDetail(Long returnId) {
        RetailReturn entity = getById(returnId);
        return entity != null ? toDTO(entity) : null;
    }

    private String generateDocNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = System.currentTimeMillis() % 10000;
        return "TH" + date + String.format("%04d", seq);
    }

    private RetailReturnDTO toDTO(RetailReturn entity) {
        RetailReturnDTO dto = new RetailReturnDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getAuditTime() != null) {
            dto.setAuditTime(entity.getAuditTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (entity.getCreateTime() != null) {
            dto.setCreateTime(entity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return dto;
    }
}
