package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeOptionDTO;
import com.duoduo.jxc.entity.SizeOption;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SizeOptionMapper;
import com.duoduo.jxc.service.SizeOptionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeOptionServiceImpl extends ServiceImpl<SizeOptionMapper, SizeOption> implements SizeOptionService {

    @Override
    public PageResult<SizeOptionDTO> pageQuery(PageQuery query) {
        Page<SizeOption> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<SizeOption> wrapper = new LambdaQueryWrapper<>();
        
        Object keywordObj = query.getParams().get("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            String keyword = keywordObj.toString();
            wrapper.and(w -> w.like(SizeOption::getName, keyword)
                    .or().like(SizeOption::getCode, keyword));
        }
        
        Object categoryIdObj = query.getParams().get("categoryId");
        if (categoryIdObj != null) {
            Long categoryId = categoryIdObj instanceof Long ? (Long) categoryIdObj : Long.valueOf(categoryIdObj.toString());
            wrapper.eq(SizeOption::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(SizeOption::getOptionId);

        Page<SizeOption> resultPage = this.page(page, wrapper);
        
        List<SizeOptionDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public SizeOptionDTO getDetail(Long id) {
        SizeOption option = this.getById(id);
        if (option == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(option);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SizeOptionDTO dto) {
        if (dto.getCategoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码类别不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码选项名称不能为空");
        }
        
        checkDuplicate(dto.getCategoryId(), dto.getName(), dto.getCode(), null);
        
        SizeOption option = new SizeOption();
        BeanUtils.copyProperties(dto, option);
        this.save(option);
        return option.getOptionId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SizeOptionDTO dto) {
        if (dto.getOptionId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码选项ID不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码类别不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码选项名称不能为空");
        }

        SizeOption exist = this.getById(dto.getOptionId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getCategoryId(), dto.getName(), dto.getCode(), dto.getOptionId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SizeOption exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(Long categoryId, String name, String code, Long excludeId) {
        if (StringUtils.hasText(name)) {
            LambdaQueryWrapper<SizeOption> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SizeOption::getCategoryId, categoryId)
                   .eq(SizeOption::getName, name);
            if (excludeId != null) {
                wrapper.ne(SizeOption::getOptionId, excludeId);
            }
            if (this.count(wrapper) > 0) {
                throw new BusinessException(BizCode.DUPLICATE, "该类别下尺码选项名称已存在");
            }
        }
        
        if (StringUtils.hasText(code)) {
            LambdaQueryWrapper<SizeOption> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SizeOption::getCategoryId, categoryId)
                   .eq(SizeOption::getCode, code);
            if (excludeId != null) {
                wrapper.ne(SizeOption::getOptionId, excludeId);
            }
            if (this.count(wrapper) > 0) {
                throw new BusinessException(BizCode.DUPLICATE, "该类别下尺码选项编码已存在");
            }
        }
    }

    private SizeOptionDTO convertToDTO(SizeOption option) {
        SizeOptionDTO dto = new SizeOptionDTO();
        BeanUtils.copyProperties(option, dto);
        return dto;
    }
}
