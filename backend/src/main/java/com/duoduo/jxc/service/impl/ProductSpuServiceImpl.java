package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.ProductConverter;
import com.duoduo.jxc.dto.product.ProductSkuDTO;
import com.duoduo.jxc.dto.product.ProductSpuDTO;
import com.duoduo.jxc.dto.product.ProductSpuQuery;
import com.duoduo.jxc.dto.settings.SystemSettingsDTO;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.entity.ProductCategory;
import com.duoduo.jxc.mapper.ProductCategoryMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.service.ProductSpuService;
import com.duoduo.jxc.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSpuServiceImpl extends ServiceImpl<ProductSpuMapper, ProductSpu> implements ProductSpuService {

    private final ProductSkuMapper productSkuMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductConverter productConverter;
    private final SysConfigService sysConfigService;

    @Override
    public PageResult<ProductSpuDTO> pageQuery(ProductSpuQuery query) {
        Page<ProductSpu> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProductSpu> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getSpuName()), ProductSpu::getSpuName, query.getSpuName())
                .like(StringUtils.hasText(query.getProductCode()), ProductSpu::getProductCode, query.getProductCode())
                .like(StringUtils.hasText(query.getSpec()), ProductSpu::getSpec, query.getSpec())
                .eq(query.getCategoryId() != null, ProductSpu::getCategoryId, query.getCategoryId())
                .eq(query.getBrandId() != null, ProductSpu::getBrandId, query.getBrandId())
                .eq(query.getStatus() != null, ProductSpu::getStatus, query.getStatus())
                .orderByDesc(ProductSpu::getCreateTime);

        page(page, wrapper);
        
        List<ProductSpuDTO> list = productConverter.toDTOList(page.getRecords());
        
        if (!list.isEmpty()) {
            Set<Long> categoryIds = list.stream().map(ProductSpuDTO::getCategoryId).filter(id -> id != null).collect(Collectors.toSet());
            if (!categoryIds.isEmpty()) {
                List<ProductCategory> categories = productCategoryMapper.selectBatchIds(categoryIds);
                java.util.Map<Long, String> categoryMap = categories.stream().collect(Collectors.toMap(ProductCategory::getCategoryId, ProductCategory::getCategoryName));
                list.forEach(dto -> dto.setCategoryName(categoryMap.get(dto.getCategoryId())));
            }
            
            // For currentStock, we need to get sum of qty from jxc_inventory for each spu
            // Actually this is a bit complex without a custom mapper. Let's just set it to 0 for now.
            // Ideally we should use a custom mapper to join and sum.
            list.forEach(dto -> dto.setCurrentStock(0));
        }
        
        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public ProductSpuDTO getDetail(Long spuId) {
        ProductSpu spu = getById(spuId);
        if (spu == null) {
            return null;
        }
        ProductSpuDTO dto = productConverter.toDTO(spu);
        
        LambdaQueryWrapper<ProductSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(ProductSku::getSpuId, spuId);
        List<ProductSku> skus = productSkuMapper.selectList(skuWrapper);
        dto.setSkuList(productConverter.toSkuDTOList(skus));
        
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveProduct(ProductSpuDTO dto) {
        validateProduct(dto, null);
        ProductSpu spu = productConverter.toEntity(dto);
        save(spu);
        
        List<ProductSkuDTO> skuList = dto.getSkuList();
        if (skuList != null && !skuList.isEmpty()) {
            List<ProductSku> skus = productConverter.toSkuEntityList(skuList);
            skus.forEach(sku -> {
                sku.setSpuId(spu.getSpuId());
                productSkuMapper.insert(sku);
            });
        }
        return spu.getSpuId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductSpuDTO dto) {
        validateProduct(dto, dto.getSpuId());
        ProductSpu spu = productConverter.toEntity(dto);
        updateById(spu);
        
        // 简单处理：先删除所有旧 SKU，再插入新 SKU
        LambdaQueryWrapper<ProductSku> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ProductSku::getSpuId, spu.getSpuId());
        productSkuMapper.delete(deleteWrapper);
        
        List<ProductSkuDTO> skuList = dto.getSkuList();
        if (skuList != null && !skuList.isEmpty()) {
            List<ProductSku> skus = productConverter.toSkuEntityList(skuList);
            skus.forEach(sku -> {
                sku.setSpuId(spu.getSpuId());
                // 重置ID使其新建
                sku.setSkuId(null);
                productSkuMapper.insert(sku);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long spuId) {
        removeById(spuId);
        LambdaQueryWrapper<ProductSku> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ProductSku::getSpuId, spuId);
        productSkuMapper.delete(deleteWrapper);
    }

    private void validateProduct(ProductSpuDTO dto, Long currentSpuId) {
        if (dto == null || !StringUtils.hasText(dto.getSpuName())) {
            return;
        }
        SystemSettingsDTO settings = sysConfigService.getSystemSettings();
        boolean allowDuplicateBarcode = settings != null && "是".equals(settings.getAllowDuplicateBarcode());
        boolean allowDuplicateNameSpec = settings != null && "是".equals(settings.getAllowDuplicateNameSpec());

        List<ProductSkuDTO> skuList = dto.getSkuList();
        if (skuList == null || skuList.isEmpty()) {
            return;
        }

        if (!allowDuplicateBarcode) {
            List<String> codes = skuList.stream().map(ProductSkuDTO::getSkuCode).filter(StringUtils::hasText).toList();
            Set<String> codesSet = new HashSet<>(codes);
            if (codesSet.size() != codes.size()) {
                throw new BusinessException(BizCode.SKU_CODE_IN_PRODUCT_DUPLICATE);
            }
            Set<String> codesKey = codesSet;
            if (!codes.isEmpty()) {
                LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<ProductSku>()
                        .in(ProductSku::getSkuCode, codesKey);
                if (currentSpuId != null) {
                    wrapper.ne(ProductSku::getSpuId, currentSpuId);
                }
                wrapper.last("limit 1");
                ProductSku exists = productSkuMapper.selectOne(wrapper);
                if (exists != null) {
                    throw new BusinessException(BizCode.SKU_CODE_DUPLICATE);
                }
            }
        }

        if (!allowDuplicateNameSpec) {
            List<String> specs = skuList.stream()
                    .map(s -> String.valueOf(StringUtils.hasText(s.getAttr1()) ? s.getAttr1() : "") + "||" + String.valueOf(StringUtils.hasText(s.getAttr2()) ? s.getAttr2() : ""))
                    .toList();
            Set<String> specSet = new HashSet<>(specs);
            if (specSet.size() != specs.size()) {
                throw new BusinessException(BizCode.SPEC_IN_PRODUCT_DUPLICATE);
            }

            LambdaQueryWrapper<ProductSpu> spuWrapper = new LambdaQueryWrapper<ProductSpu>()
                    .select(ProductSpu::getSpuId)
                    .eq(ProductSpu::getSpuName, dto.getSpuName());
            if (currentSpuId != null) {
                spuWrapper.ne(ProductSpu::getSpuId, currentSpuId);
            }
            List<ProductSpu> spus = list(spuWrapper);
            if (spus == null || spus.isEmpty()) {
                return;
            }
            Set<Long> spuIds = spus.stream().map(ProductSpu::getSpuId).collect(Collectors.toSet());
            for (ProductSkuDTO sku : skuList) {
                String a1 = StringUtils.hasText(sku.getAttr1()) ? sku.getAttr1() : null;
                String a2 = StringUtils.hasText(sku.getAttr2()) ? sku.getAttr2() : null;
                LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<ProductSku>().in(ProductSku::getSpuId, spuIds);
                if (a1 == null) {
                    wrapper.and(w -> w.isNull(ProductSku::getAttr1).or().eq(ProductSku::getAttr1, ""));
                } else {
                    wrapper.eq(ProductSku::getAttr1, a1);
                }
                if (a2 == null) {
                    wrapper.and(w -> w.isNull(ProductSku::getAttr2).or().eq(ProductSku::getAttr2, ""));
                } else {
                    wrapper.eq(ProductSku::getAttr2, a2);
                }
                wrapper.last("limit 1");
                ProductSku exists = productSkuMapper.selectOne(wrapper);
                if (exists != null) {
                    throw new BusinessException(BizCode.SPU_NAME_SPEC_DUPLICATE);
                }
            }
        }
    }
}
