package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ProductBrandDTO;
import com.duoduo.jxc.entity.ProductBrand;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.ProductBrandMapper;
import com.duoduo.jxc.service.ProductBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductBrandServiceImpl extends ServiceImpl<ProductBrandMapper, ProductBrand> implements ProductBrandService {

    @Override
    public PageResult<ProductBrandDTO> pageQuery(PageQuery query) {
        Page<ProductBrand> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<ProductBrand> wrapper = new LambdaQueryWrapper<>();
        Object keywordObj = query.getParam("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            String keyword = keywordObj.toString();
            wrapper.like(ProductBrand::getBrandName, keyword);
        }
        Object statusObj = query.getParam("status");
        if (statusObj != null) {
            wrapper.eq(ProductBrand::getStatus, statusObj);
        }
        wrapper.orderByAsc(ProductBrand::getSort).orderByDesc(ProductBrand::getBrandId);

        Page<ProductBrand> resultPage = this.page(page, wrapper);
        
        List<ProductBrandDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public ProductBrandDTO getDetail(Long id) {
        ProductBrand brand = this.getById(id);
        if (brand == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(brand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductBrandDTO dto) {
        if (!StringUtils.hasText(dto.getBrandName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "品牌名称不能为空");
        }
        
        checkDuplicate(dto.getBrandName(), null);
        
        ProductBrand brand = new ProductBrand();
        BeanUtils.copyProperties(dto, brand);
        if (brand.getSort() == null) {
            brand.setSort(0);
        }
        if (brand.getStatus() == null) {
            brand.setStatus(1);
        }
        this.save(brand);
        return brand.getBrandId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductBrandDTO dto) {
        if (dto.getBrandId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "品牌ID不能为空");
        }
        if (!StringUtils.hasText(dto.getBrandName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "品牌名称不能为空");
        }

        ProductBrand exist = this.getById(dto.getBrandId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        
        checkDuplicate(dto.getBrandName(), dto.getBrandId());
        
        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProductBrand exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }
    
    private void checkDuplicate(String brandName, Long excludeId) {
        LambdaQueryWrapper<ProductBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductBrand::getBrandName, brandName);
        if (excludeId != null) {
            wrapper.ne(ProductBrand::getBrandId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "品牌名称已存在");
        }
    }
    
    private ProductBrandDTO convertToDTO(ProductBrand entity) {
        ProductBrandDTO dto = new ProductBrandDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
