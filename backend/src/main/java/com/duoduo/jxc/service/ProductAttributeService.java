package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.product.ProductAttributeDTO;
import com.duoduo.jxc.entity.ProductAttribute;

import java.util.List;

public interface ProductAttributeService extends IService<ProductAttribute> {
    
    List<ProductAttributeDTO> listWithValues();
    
    void saveAttribute(ProductAttributeDTO dto);
    
    void updateAttribute(ProductAttributeDTO dto);
    
    void deleteAttribute(Long id);
    
    void saveAttributeValue(Long attributeId, String value);
    
    void updateAttributeValue(Long id, String value);
    
    void deleteAttributeValue(Long id);
    
    void batchSaveAttributeValues(Long attributeId, List<String> values);
}
