package com.duoduo.jxc.product;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.product.ProductCategoryDTO;
import com.duoduo.jxc.entity.ProductCategory;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.ProductCategoryMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.service.ProductCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class ProductCategoryServiceTest {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private ProductSpuMapper productSpuMapper;

    @Test
    void addCategory_shouldReturnId() {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("测试分类");
        dto.setSort(1);

        Long id = productCategoryService.addCategory(dto);
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void addCategory_withParent_shouldSetParentId() {
        ProductCategory parent = new ProductCategory();
        parent.setParentId(0L);
        parent.setCategoryName("父分类");
        parent.setSort(1);
        parent.setStatus(1);
        productCategoryMapper.insert(parent);

        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("子分类");
        dto.setParentId(parent.getCategoryId());
        dto.setSort(1);

        Long id = productCategoryService.addCategory(dto);
        assertNotNull(id);

        ProductCategory saved = productCategoryMapper.selectById(id);
        assertEquals(parent.getCategoryId(), saved.getParentId());
    }

    @Test
    void addCategory_duplicateName_shouldThrow() {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("重复名称测试");
        dto.setSort(1);
        productCategoryService.addCategory(dto);

        ProductCategoryDTO dto2 = new ProductCategoryDTO();
        dto2.setName("重复名称测试");
        dto2.setSort(2);

        assertThrows(BusinessException.class, () -> productCategoryService.addCategory(dto2));
    }

    @Test
    void updateCategory_shouldUpdateFields() {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("待更新分类");
        dto.setSort(1);
        Long id = productCategoryService.addCategory(dto);

        ProductCategoryDTO update = new ProductCategoryDTO();
        update.setId(id);
        update.setName("已更新分类");
        update.setSort(5);
        productCategoryService.updateCategory(update);

        ProductCategory updated = productCategoryMapper.selectById(id);
        assertEquals("已更新分类", updated.getCategoryName());
        assertEquals(5, updated.getSort());
    }

    @Test
    void updateCategory_nullId_shouldThrow() {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("测试");
        dto.setSort(1);
        assertThrows(BusinessException.class, () -> productCategoryService.updateCategory(dto));
    }

    @Test
    void updateCategory_selfParent_shouldThrow() {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setName("自引用测试");
        dto.setSort(1);
        Long id = productCategoryService.addCategory(dto);

        ProductCategoryDTO update = new ProductCategoryDTO();
        update.setId(id);
        update.setName("自引用测试");
        update.setParentId(id);
        update.setSort(1);
        assertThrows(BusinessException.class, () -> productCategoryService.updateCategory(update));
    }

    @Test
    void updateCategory_notFound_shouldThrow() {
        ProductCategoryDTO update = new ProductCategoryDTO();
        update.setId(999999L);
        update.setName("不存在");
        update.setSort(1);
        assertThrows(BusinessException.class, () -> productCategoryService.updateCategory(update));
    }

    @Test
    void deleteCategory_hasChild_shouldThrow() {
        ProductCategory parent = new ProductCategory();
        parent.setParentId(0L);
        parent.setCategoryName("父分类删");
        parent.setSort(1);
        parent.setStatus(1);
        productCategoryMapper.insert(parent);

        ProductCategory child = new ProductCategory();
        child.setParentId(parent.getCategoryId());
        child.setCategoryName("子分类删");
        child.setSort(1);
        child.setStatus(1);
        productCategoryMapper.insert(child);

        assertThrows(BusinessException.class, () -> productCategoryService.deleteCategory(parent.getCategoryId()));
    }

    @Test
    void deleteCategory_hasProducts_shouldThrow() {
        ProductCategory cat = new ProductCategory();
        cat.setParentId(0L);
        cat.setCategoryName("商品分类删");
        cat.setSort(1);
        cat.setStatus(1);
        productCategoryMapper.insert(cat);

        ProductSpu spu = new ProductSpu();
        spu.setSpuName("测试商品");
        spu.setCategoryId(cat.getCategoryId());
        spu.setUnit("件");
        spu.setStatus(1);
        productSpuMapper.insert(spu);

        assertThrows(BusinessException.class, () -> productCategoryService.deleteCategory(cat.getCategoryId()));
    }

    @Test
    void deleteCategory_success() {
        ProductCategory cat = new ProductCategory();
        cat.setParentId(0L);
        cat.setCategoryName("可删除分类");
        cat.setSort(1);
        cat.setStatus(1);
        productCategoryMapper.insert(cat);

        productCategoryService.deleteCategory(cat.getCategoryId());
        assertNull(productCategoryMapper.selectById(cat.getCategoryId()));
    }
}
