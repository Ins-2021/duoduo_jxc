package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FabricConverter;
import com.duoduo.jxc.dto.fabric.FabricDTO;
import com.duoduo.jxc.dto.fabric.FabricQuery;
import com.duoduo.jxc.entity.Fabric;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.mapper.FabricMapper;
import com.duoduo.jxc.service.FabricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FabricServiceImpl extends ServiceImpl<FabricMapper, Fabric> implements FabricService {

    private final FabricConverter converter;

    @Override
    public PageResult<FabricDTO> pageQuery(FabricQuery query) {
        Page<Fabric> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Fabric> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getFabricCode()), Fabric::getFabricCode, query.getFabricCode())
               .like(StringUtils.hasText(query.getFabricName()), Fabric::getFabricName, query.getFabricName())
               .eq(StringUtils.hasText(query.getFabricType()), Fabric::getFabricType, query.getFabricType())
               .eq(query.getSupplierId() != null, Fabric::getSupplierId, query.getSupplierId())
               .eq(query.getStatus() != null, Fabric::getStatus, query.getStatus())
               .orderByDesc(Fabric::getCreateTime);
        page(page, wrapper);
        List<FabricDTO> dtoList = converter.toDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public FabricDTO getDetail(Long id) {
        Fabric fabric = getById(id);
        if (fabric == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return converter.toDTO(fabric);
    }

    @Override
    public Long create(FabricDTO dto) {
        Fabric fabric = converter.toEntity(dto);
        fabric.setStatus(1);
        save(fabric);
        return fabric.getFabricId();
    }

    @Override
    public void update(FabricDTO dto) {
        if (dto.getFabricId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST);
        }
        Fabric fabric = converter.toEntity(dto);
        updateById(fabric);
    }

    @Override
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public List<FabricDTO> listAll() {
        LambdaQueryWrapper<Fabric> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Fabric::getStatus, 1).orderByAsc(Fabric::getFabricCode);
        return converter.toDTOList(list(wrapper));
    }
}
