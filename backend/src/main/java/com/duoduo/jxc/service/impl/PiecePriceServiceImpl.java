package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PiecePriceDTO;
import com.duoduo.jxc.dto.wage.PiecePriceQuery;
import com.duoduo.jxc.entity.wage.PiecePrice;
import com.duoduo.jxc.mapper.PiecePriceMapper;
import com.duoduo.jxc.service.PiecePriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PiecePriceServiceImpl extends ServiceImpl<PiecePriceMapper, PiecePrice> implements PiecePriceService {

    @Override
    public PageResult<PiecePriceDTO> pageQuery(PiecePriceQuery query) {
        Page<PiecePrice> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PiecePrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getStyleId() != null, PiecePrice::getStyleId, query.getStyleId())
               .like(StringUtils.hasText(query.getStyleCode()), PiecePrice::getStyleCode, query.getStyleCode())
               .eq(StringUtils.hasText(query.getProcessCode()), PiecePrice::getProcessCode, query.getProcessCode())
               .eq(query.getEmployeeId() != null, PiecePrice::getEmployeeId, query.getEmployeeId())
               .eq(query.getPriceType() != null, PiecePrice::getPriceType, query.getPriceType())
               .eq(query.getStatus() != null, PiecePrice::getStatus, query.getStatus())
               .ge(query.getEffectiveDateFrom() != null, PiecePrice::getEffectiveDate, query.getEffectiveDateFrom())
               .le(query.getEffectiveDateTo() != null, PiecePrice::getEffectiveDate, query.getEffectiveDateTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(PiecePrice::getStyleCode, query.getKeyword())
                    .or().like(PiecePrice::getStyleName, query.getKeyword())
                    .or().like(PiecePrice::getProcessName, query.getKeyword()))
               .orderByDesc(PiecePrice::getCreateTime);
        page(page, wrapper);
        List<PiecePriceDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PiecePriceDTO getDetail(Long id) {
        PiecePrice entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PiecePriceDTO dto) {
        PiecePrice entity = new PiecePrice();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        save(entity);
        return entity.getPriceId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PiecePriceDTO dto) {
        PiecePrice entity = new PiecePrice();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private PiecePriceDTO toDTO(PiecePrice entity) {
        PiecePriceDTO dto = new PiecePriceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
