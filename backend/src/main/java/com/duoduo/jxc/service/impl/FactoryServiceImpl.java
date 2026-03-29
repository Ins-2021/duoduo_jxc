package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.FactoryDTO;
import com.duoduo.jxc.entity.Factory;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.FactoryMapper;
import com.duoduo.jxc.service.FactoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FactoryServiceImpl extends ServiceImpl<FactoryMapper, Factory> implements FactoryService {

    @Override
    public PageResult<FactoryDTO> pageQuery(PageQuery query) {
        Page<Factory> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Factory> wrapper = new LambdaQueryWrapper<>();
        Object keywordObj = query.getParam("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            String keyword = keywordObj.toString();
            wrapper.like(Factory::getName, keyword)
                   .or()
                   .like(Factory::getCode, keyword);
        }
        wrapper.orderByDesc(Factory::getFactoryId);

        Page<Factory> resultPage = this.page(page, wrapper);

        List<FactoryDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public FactoryDTO getDetail(Long id) {
        Factory factory = this.getById(id);
        if (factory == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(factory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FactoryDTO dto) {
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "工厂编码和名称不能为空");
        }

        checkDuplicate(dto.getCode(), null);

        Factory factory = new Factory();
        BeanUtils.copyProperties(dto, factory);
        this.save(factory);
        return factory.getFactoryId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FactoryDTO dto) {
        if (dto.getFactoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "工厂ID不能为空");
        }
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "工厂编码和名称不能为空");
        }

        Factory exist = this.getById(dto.getFactoryId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getCode(), dto.getFactoryId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Factory exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(String code, Long excludeId) {
        LambdaQueryWrapper<Factory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Factory::getCode, code);
        if (excludeId != null) {
            wrapper.ne(Factory::getFactoryId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "工厂编码已存在");
        }
    }

    private FactoryDTO convertToDTO(Factory factory) {
        FactoryDTO dto = new FactoryDTO();
        BeanUtils.copyProperties(factory, dto);
        return dto;
    }
}
