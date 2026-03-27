package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.dto.product.ProductAttributeDTO;
import com.duoduo.jxc.dto.product.ProductAttributeValueDTO;
import com.duoduo.jxc.entity.ProductAttribute;
import com.duoduo.jxc.entity.ProductAttributeValue;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.ProductAttributeMapper;
import com.duoduo.jxc.mapper.ProductAttributeValueMapper;
import com.duoduo.jxc.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

    private final ProductAttributeValueMapper attributeValueMapper;

    @Override
    public List<ProductAttributeDTO> listWithValues() {
        List<ProductAttribute> attributes = this.list();
        return attributes.stream().map(attr -> {
            ProductAttributeDTO dto = new ProductAttributeDTO();
            dto.setId(attr.getId());
            dto.setName(attr.getName());
            dto.setSort(attr.getSort());
            
            List<ProductAttributeValue> values = attributeValueMapper.selectList(
                    new LambdaQueryWrapper<ProductAttributeValue>()
                            .eq(ProductAttributeValue::getAttributeId, attr.getId())
                            .orderByAsc(ProductAttributeValue::getSort)
            );
            
            List<ProductAttributeValueDTO> valueDTOs = values.stream().map(v -> {
                ProductAttributeValueDTO vDto = new ProductAttributeValueDTO();
                vDto.setId(v.getId());
                vDto.setAttributeId(v.getAttributeId());
                vDto.setValue(v.getValue());
                vDto.setSort(v.getSort());
                return vDto;
            }).collect(Collectors.toList());
            
            dto.setOptions(valueDTOs);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void saveAttribute(ProductAttributeDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "属性名称不能为空");
        }
        ProductAttribute entity = new ProductAttribute();
        entity.setName(dto.getName());
        entity.setSort(dto.getSort() != null ? dto.getSort() : 0);
        this.save(entity);
    }

    @Override
    public void updateAttribute(ProductAttributeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "属性ID不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "属性名称不能为空");
        }
        ProductAttribute entity = this.getById(dto.getId());
        if (entity == null) {
            throw new BusinessException(BizCode.ATTRIBUTE_NOT_FOUND);
        }
        entity.setName(dto.getName());
        if (dto.getSort() != null) {
            entity.setSort(dto.getSort());
        }
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttribute(Long id) {
        this.removeById(id);
        attributeValueMapper.delete(new LambdaQueryWrapper<ProductAttributeValue>()
                .eq(ProductAttributeValue::getAttributeId, id));
    }

    @Override
    public void saveAttributeValue(Long attributeId, String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(BizCode.BAD_REQUEST, "选项名称不能为空");
        }
        
        Long count = attributeValueMapper.selectCount(new LambdaQueryWrapper<ProductAttributeValue>()
                .eq(ProductAttributeValue::getAttributeId, attributeId)
                .eq(ProductAttributeValue::getValue, value));
        if (count > 0) {
            throw new BusinessException(BizCode.ATTRIBUTE_OPTION_DUPLICATE);
        }
        
        ProductAttributeValue entity = new ProductAttributeValue();
        entity.setAttributeId(attributeId);
        entity.setValue(value);
        entity.setSort(0);
        attributeValueMapper.insert(entity);
    }

    @Override
    public void updateAttributeValue(Long id, String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(BizCode.BAD_REQUEST, "选项名称不能为空");
        }
        ProductAttributeValue entity = attributeValueMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.ATTRIBUTE_OPTION_NOT_FOUND);
        }
        
        Long count = attributeValueMapper.selectCount(new LambdaQueryWrapper<ProductAttributeValue>()
                .eq(ProductAttributeValue::getAttributeId, entity.getAttributeId())
                .eq(ProductAttributeValue::getValue, value)
                .ne(ProductAttributeValue::getId, id));
        if (count > 0) {
            throw new BusinessException(BizCode.ATTRIBUTE_OPTION_DUPLICATE);
        }
        
        entity.setValue(value);
        attributeValueMapper.updateById(entity);
    }

    @Override
    public void deleteAttributeValue(Long id) {
        attributeValueMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveAttributeValues(Long attributeId, List<String> values) {
        // 先删除旧的
        attributeValueMapper.delete(new LambdaQueryWrapper<ProductAttributeValue>()
                .eq(ProductAttributeValue::getAttributeId, attributeId));
                
        // 插入新的
        if (values != null && !values.isEmpty()) {
            for (int i = 0; i < values.size(); i++) {
                String val = values.get(i);
                if (StringUtils.hasText(val)) {
                    ProductAttributeValue entity = new ProductAttributeValue();
                    entity.setAttributeId(attributeId);
                    entity.setValue(val.trim());
                    entity.setSort(i);
                    attributeValueMapper.insert(entity);
                }
            }
        }
    }
}