package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeChartDTO;
import com.duoduo.jxc.entity.SizeChart;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SizeChartMapper;
import com.duoduo.jxc.service.SizeChartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeChartServiceImpl extends ServiceImpl<SizeChartMapper, SizeChart> implements SizeChartService {

    @Override
    public PageResult<SizeChartDTO> pageQuery(PageQuery query) {
        Page<SizeChart> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<SizeChart> wrapper = new LambdaQueryWrapper<>();
        Object standardIdObj = query.getParam("standardId");
        if (standardIdObj != null && StringUtils.hasText(standardIdObj.toString())) {
            wrapper.eq(SizeChart::getStandardId, Long.valueOf(standardIdObj.toString()));
        }
        Object categoryIdObj = query.getParam("categoryId");
        if (categoryIdObj != null && StringUtils.hasText(categoryIdObj.toString())) {
            wrapper.eq(SizeChart::getCategoryId, Long.valueOf(categoryIdObj.toString()));
        }
        wrapper.orderByDesc(SizeChart::getChartId);

        Page<SizeChart> resultPage = this.page(page, wrapper);
        
        List<SizeChartDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public SizeChartDTO getDetail(Long id) {
        SizeChart chart = this.getById(id);
        if (chart == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(chart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SizeChartDTO dto) {
        if (dto.getStandardId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "标准ID不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码组ID不能为空");
        }
        if (!StringUtils.hasText(dto.getSizeCode())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码编码不能为空");
        }
        
        checkDuplicate(dto.getStandardId(), dto.getCategoryId(), dto.getSizeCode(), null);
        
        SizeChart chart = new SizeChart();
        BeanUtils.copyProperties(dto, chart);
        this.save(chart);
        return chart.getChartId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SizeChartDTO dto) {
        if (dto.getChartId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码表ID不能为空");
        }
        if (dto.getStandardId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "标准ID不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码组ID不能为空");
        }
        if (!StringUtils.hasText(dto.getSizeCode())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码编码不能为空");
        }

        SizeChart exist = this.getById(dto.getChartId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getStandardId(), dto.getCategoryId(), dto.getSizeCode(), dto.getChartId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SizeChart exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(Long standardId, Long categoryId, String sizeCode, Long excludeId) {
        LambdaQueryWrapper<SizeChart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SizeChart::getStandardId, standardId)
               .eq(SizeChart::getCategoryId, categoryId)
               .eq(SizeChart::getSizeCode, sizeCode);
        if (excludeId != null) {
            wrapper.ne(SizeChart::getChartId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "该标准和尺码组下已存在相同的尺码编码");
        }
    }

    private SizeChartDTO convertToDTO(SizeChart chart) {
        SizeChartDTO dto = new SizeChartDTO();
        BeanUtils.copyProperties(chart, dto);
        return dto;
    }
}
