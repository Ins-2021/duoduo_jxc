package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.CutBundleDTO;
import com.duoduo.jxc.dto.production.CutBundleQuery;
import com.duoduo.jxc.entity.CutBundle;
import com.duoduo.jxc.mapper.CutBundleMapper;
import com.duoduo.jxc.service.CutBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CutBundleServiceImpl extends ServiceImpl<CutBundleMapper, CutBundle> implements CutBundleService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<CutBundleDTO> pageQuery(CutBundleQuery query) {
        Page<CutBundle> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CutBundle> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getBundleNo()), CutBundle::getBundleNo, query.getBundleNo())
               .eq(query.getCuttingPlanId() != null, CutBundle::getCuttingPlanId, query.getCuttingPlanId())
               .eq(StringUtils.hasText(query.getSize()), CutBundle::getSize, query.getSize())
               .eq(StringUtils.hasText(query.getColor()), CutBundle::getColor, query.getColor())
               .eq(StringUtils.hasText(query.getStatus()), CutBundle::getStatus, query.getStatus())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(CutBundle::getBundleNo, query.getKeyword())
                    .or().like(CutBundle::getQrCode, query.getKeyword()))
               .orderByDesc(CutBundle::getCreateTime);
        page(page, wrapper);
        List<CutBundleDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CutBundleDTO getDetail(Long id) {
        CutBundle entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CutBundleDTO dto) {
        CutBundle entity = new CutBundle();
        BeanUtils.copyProperties(dto, entity);
        entity.setBundleNo(generateBundleNo());
        if (entity.getStatus() == null) {
            entity.setStatus("pending");
        }
        if (entity.getQrCode() == null) {
            entity.setQrCode(UUID.randomUUID().toString().replace("-", "").toUpperCase());
        }
        entity.setCreateTime(LocalDateTime.now());
        save(entity);
        return entity.getBundleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CutBundleDTO dto) {
        CutBundle entity = new CutBundle();
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
    public void updateStatus(Long id, String status) {
        CutBundle entity = new CutBundle();
        entity.setBundleId(id);
        entity.setStatus(status);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignProcess(Long id, Long processId) {
        CutBundle entity = new CutBundle();
        entity.setBundleId(id);
        entity.setProcessId(processId);
        entity.setStatus("assigned");
        updateById(entity);
    }

    private CutBundleDTO toDTO(CutBundle entity) {
        CutBundleDTO dto = new CutBundleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateBundleNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "CB" + dateStr + randomStr;
    }
}
