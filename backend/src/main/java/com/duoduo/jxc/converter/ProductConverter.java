package com.duoduo.jxc.converter;

import com.duoduo.jxc.dto.product.ProductSkuDTO;
import com.duoduo.jxc.dto.product.ProductSpuDTO;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.ProductSpu;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductConverter {
    ProductConverter INSTANCE = Mappers.getMapper(ProductConverter.class);

    ProductSpu toEntity(ProductSpuDTO dto);
    ProductSpuDTO toDTO(ProductSpu entity);
    List<ProductSpuDTO> toDTOList(List<ProductSpu> list);

    ProductSku toSkuEntity(ProductSkuDTO dto);
    ProductSkuDTO toSkuDTO(ProductSku entity);
    List<ProductSkuDTO> toSkuDTOList(List<ProductSku> list);
    List<ProductSku> toSkuEntityList(List<ProductSkuDTO> list);
}
