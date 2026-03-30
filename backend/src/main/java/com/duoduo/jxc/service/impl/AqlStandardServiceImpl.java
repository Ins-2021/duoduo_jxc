package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.AqlResultDTO;
import com.duoduo.jxc.dto.AqlStandardDTO;
import com.duoduo.jxc.dto.AqlStandardQuery;
import com.duoduo.jxc.entity.AqlStandard;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.AqlStandardMapper;
import com.duoduo.jxc.service.AqlStandardService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AqlStandardServiceImpl extends ServiceImpl<AqlStandardMapper, AqlStandard> implements AqlStandardService {

    @Override
    public PageResult<AqlStandardDTO> pageQuery(AqlStandardQuery query) {
        Page<AqlStandard> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<AqlStandard> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(StringUtils.hasText(query.getLevel()), AqlStandard::getLevel, query.getLevel());
        wrapper.orderByAsc(AqlStandard::getLevel, AqlStandard::getBatchSizeMin);

        page(page, wrapper);

        List<AqlStandardDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public AqlStandardDTO getDetail(Long id) {
        AqlStandard entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(AqlStandardDTO dto) {
        AqlStandard entity = new AqlStandard();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getAqlId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AqlStandardDTO dto) {
        AqlStandard entity = new AqlStandard();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public AqlResultDTO calculateSampleSize(Integer batchSize, String aqlLevel) {
        LambdaQueryWrapper<AqlStandard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AqlStandard::getLevel, aqlLevel)
               .le(AqlStandard::getBatchSizeMin, batchSize)
               .ge(AqlStandard::getBatchSizeMax, batchSize);
        
        AqlStandard standard = getOne(wrapper);
        if (standard == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "未找到对应的AQL标准");
        }
        
        AqlResultDTO result = new AqlResultDTO();
        result.setSampleSize(standard.getSampleSize());
        result.setAcceptNum(standard.getAcceptNum());
        result.setRejectNum(standard.getRejectNum());
        return result;
    }

    private AqlStandardDTO convertToDTO(AqlStandard entity) {
        AqlStandardDTO dto = new AqlStandardDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
