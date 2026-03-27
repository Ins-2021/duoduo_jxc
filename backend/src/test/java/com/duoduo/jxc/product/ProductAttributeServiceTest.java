package com.duoduo.jxc.product;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.product.ProductAttributeDTO;
import com.duoduo.jxc.entity.ProductAttribute;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.ProductAttributeMapper;
import com.duoduo.jxc.service.ProductAttributeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class ProductAttributeServiceTest {

    @Autowired
    private ProductAttributeService productAttributeService;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Test
    void saveAttribute_shouldCreateNewAttribute() {
        ProductAttributeDTO dto = new ProductAttributeDTO();
        dto.setName("测试属性_" + System.currentTimeMillis());
        dto.setSort(0);
        assertDoesNotThrow(() -> productAttributeService.saveAttribute(dto));

        List<ProductAttribute> attrs = productAttributeMapper.selectList(null);
        assertTrue(attrs.stream().anyMatch(a -> a.getName().equals(dto.getName())));
    }

    @Test
    void saveAttribute_shouldThrowWhenNameBlank() {
        ProductAttributeDTO dto = new ProductAttributeDTO();
        assertThrows(BusinessException.class, () -> productAttributeService.saveAttribute(dto));
    }

    @Test
    void updateAttribute_shouldThrowWhenIdNull() {
        ProductAttributeDTO dto = new ProductAttributeDTO();
        dto.setName("测试");
        assertThrows(BusinessException.class, () -> productAttributeService.updateAttribute(dto));
    }

    @Test
    void updateAttribute_shouldThrowWhenNotFound() {
        ProductAttributeDTO dto = new ProductAttributeDTO();
        dto.setId(999999L);
        dto.setName("不存在的属性");
        assertThrows(BusinessException.class, () -> productAttributeService.updateAttribute(dto));
    }

    @Test
    void deleteAttribute_shouldRemoveAttributeAndValues() {
        ProductAttribute attr = new ProductAttribute();
        attr.setName("待删除属性_" + System.currentTimeMillis());
        attr.setSort(0);
        productAttributeMapper.insert(attr);

        assertDoesNotThrow(() -> productAttributeService.deleteAttribute(attr.getId()));
        assertNull(productAttributeMapper.selectById(attr.getId()));
    }

    @Test
    void saveAttributeValue_shouldThrowWhenValueBlank() {
        assertThrows(BusinessException.class, () -> productAttributeService.saveAttributeValue(1L, ""));
    }

    @Test
    void saveAttributeValue_shouldThrowWhenDuplicate() {
        ProductAttribute attr = new ProductAttribute();
        attr.setName("唯一性测试_" + System.currentTimeMillis());
        attr.setSort(0);
        productAttributeMapper.insert(attr);

        productAttributeService.saveAttributeValue(attr.getId(), "红色");
        assertThrows(BusinessException.class, () -> productAttributeService.saveAttributeValue(attr.getId(), "红色"));
    }

    @Test
    void updateAttributeValue_shouldThrowWhenNotFound() {
        assertThrows(BusinessException.class, () -> productAttributeService.updateAttributeValue(999999L, "值"));
    }

    @Test
    void listWithValues_shouldReturnAttributesWithOptions() {
        List<ProductAttributeDTO> list = productAttributeService.listWithValues();
        assertNotNull(list);
        // Each attribute should have options list (may be empty)
        list.forEach(dto -> assertNotNull(dto.getOptions()));
    }
}
