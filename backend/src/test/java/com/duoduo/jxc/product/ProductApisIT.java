package com.duoduo.jxc.product;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.product.ProductCategoryTreeNode;
import com.duoduo.jxc.dto.product.ProductSkuSelectDTO;
import com.duoduo.jxc.dto.product.ProductSkuSelectQuery;
import com.duoduo.jxc.entity.Inventory;
import com.duoduo.jxc.entity.ProductBrand;
import com.duoduo.jxc.entity.ProductCategory;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.mapper.InventoryMapper;
import com.duoduo.jxc.mapper.ProductBrandMapper;
import com.duoduo.jxc.mapper.ProductCategoryMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.service.ProductCategoryService;
import com.duoduo.jxc.service.ProductSelectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
public class ProductApisIT {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private ProductBrandMapper productBrandMapper;

    @Autowired
    private ProductSpuMapper productSpuMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductSelectService productSelectService;

    @Test
    void categoryTree_shouldReturnHierarchy() {
        ProductCategory root = new ProductCategory();
        root.setParentId(0L);
        root.setCategoryName("上衣");
        root.setSort(1);
        root.setStatus(1);
        productCategoryMapper.insert(root);

        ProductCategory child = new ProductCategory();
        child.setParentId(root.getCategoryId());
        child.setCategoryName("T恤");
        child.setSort(1);
        child.setStatus(1);
        productCategoryMapper.insert(child);

        List<ProductCategoryTreeNode> tree = productCategoryService.tree();
        assertFalse(tree.isEmpty());
        ProductCategoryTreeNode node = tree.stream().filter(n -> "上衣".equals(n.getLabel())).findFirst().orElse(null);
        assertNotNull(node);
        assertTrue(node.getChildren().stream().anyMatch(n -> "T恤".equals(n.getLabel())));
    }

    @Test
    void skuPage_shouldJoinStockAndReturnProductCode() {
        ProductBrand brand = new ProductBrand();
        brand.setBrandName("无");
        brand.setSort(0);
        brand.setStatus(1);
        productBrandMapper.insert(brand);

        ProductCategory cat = new ProductCategory();
        cat.setParentId(0L);
        cat.setCategoryName("裤子");
        cat.setSort(1);
        cat.setStatus(1);
        productCategoryMapper.insert(cat);

        ProductSpu spu = new ProductSpu();
        spu.setSpuName("小冰棉");
        spu.setBrandId(brand.getBrandId());
        spu.setCategoryId(cat.getCategoryId());
        spu.setUnit("件");
        spu.setStatus(1);
        productSpuMapper.insert(spu);

        ProductSku sku = new ProductSku();
        sku.setSpuId(spu.getSpuId());
        sku.setSkuCode("000000018");
        sku.setAttr1("黑色");
        sku.setAttr2("2XL");
        sku.setWholesalePrice(new BigDecimal("9"));
        sku.setPurchasePrice(new BigDecimal("8"));
        sku.setRetailPrice(new BigDecimal("12"));
        sku.setWarningQty(0);
        sku.setStatus(1);
        productSkuMapper.insert(sku);

        Inventory inv = new Inventory();
        inv.setWarehouseId(1L);
        inv.setSkuId(sku.getSkuId());
        inv.setQty(5);
        inventoryMapper.insert(inv);

        ProductSkuSelectQuery q = new ProductSkuSelectQuery();
        q.setPageNum(1);
        q.setPageSize(10);
        q.setKeyword("小冰棉");
        PageResult<ProductSkuSelectDTO> page = productSelectService.pageSku(q);
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
        ProductSkuSelectDTO row = page.getList().get(0);
        assertEquals("小冰棉", row.getName());
        assertEquals("黑色|2XL", row.getAttributes());
        assertEquals(5, row.getStock());
        assertNotNull(row.getProductCode());
        assertTrue(row.getProductCode().startsWith("SP"));
    }
}

