package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PayrollSheetDetailDTO;
import com.duoduo.jxc.dto.wage.PayrollSheetDetailQuery;
import com.duoduo.jxc.entity.PayrollSheetDetail;
import com.duoduo.jxc.mapper.PayrollSheetDetailMapper;
import com.duoduo.jxc.service.PayrollSheetDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollSheetDetailServiceImpl extends ServiceImpl<PayrollSheetDetailMapper, PayrollSheetDetail> implements PayrollSheetDetailService {

    @Override
    public PageResult<PayrollSheetDetailDTO> pageQuery(PayrollSheetDetailQuery query) {
        Page<PayrollSheetDetail> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PayrollSheetDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getSheetId() != null, PayrollSheetDetail::getSheetId, query.getSheetId())
               .like(StringUtils.hasText(query.getSheetNo()), PayrollSheetDetail::getSheetNo, query.getSheetNo())
               .eq(StringUtils.hasText(query.getYearMonth()), PayrollSheetDetail::getYearMonth, query.getYearMonth())
               .eq(query.getEmployeeId() != null, PayrollSheetDetail::getEmployeeId, query.getEmployeeId())
               .like(StringUtils.hasText(query.getEmployeeName()), PayrollSheetDetail::getEmployeeName, query.getEmployeeName())
               .eq(query.getDepartmentId() != null, PayrollSheetDetail::getDepartmentId, query.getDepartmentId())
               .eq(StringUtils.hasText(query.getCostType()), PayrollSheetDetail::getCostType, query.getCostType())
               .eq(query.getPayStatus() != null, PayrollSheetDetail::getPayStatus, query.getPayStatus())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(PayrollSheetDetail::getEmployeeName, query.getKeyword())
                    .or().like(PayrollSheetDetail::getEmployeeCode, query.getKeyword())
                    .or().like(PayrollSheetDetail::getSheetNo, query.getKeyword()))
               .orderByDesc(PayrollSheetDetail::getCreateTime);
        page(page, wrapper);
        List<PayrollSheetDetailDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PayrollSheetDetailDTO getDetail(Long id) {
        PayrollSheetDetail entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PayrollSheetDetailDTO dto) {
        PayrollSheetDetail entity = new PayrollSheetDetail();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getPayStatus() == null) {
            entity.setPayStatus(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PayrollSheetDetailDTO dto) {
        PayrollSheetDetail entity = new PayrollSheetDetail();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private PayrollSheetDetailDTO toDTO(PayrollSheetDetail entity) {
        PayrollSheetDetailDTO dto = new PayrollSheetDetailDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
