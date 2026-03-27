package com.duoduo.jxc.converter;

import com.duoduo.jxc.dto.purchase.PurchaseOrderDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDetailDTO;
import com.duoduo.jxc.entity.PurchaseOrder;
import com.duoduo.jxc.entity.PurchaseOrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderConverter {
    PurchaseOrderConverter INSTANCE = Mappers.getMapper(PurchaseOrderConverter.class);

    PurchaseOrder toEntity(PurchaseOrderDTO dto);
    PurchaseOrderDTO toDTO(PurchaseOrder entity);
    List<PurchaseOrderDTO> toDTOList(List<PurchaseOrder> list);

    PurchaseOrderDetail toDetailEntity(PurchaseOrderDetailDTO dto);
    PurchaseOrderDetailDTO toDetailDTO(PurchaseOrderDetail entity);
    List<PurchaseOrderDetailDTO> toDetailDTOList(List<PurchaseOrderDetail> list);
    List<PurchaseOrderDetail> toDetailEntityList(List<PurchaseOrderDetailDTO> list);
}
