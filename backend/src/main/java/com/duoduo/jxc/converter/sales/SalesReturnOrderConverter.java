package com.duoduo.jxc.converter.sales;

import com.duoduo.jxc.dto.sales.return_order.SalesReturnDetailDTO;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnOrderDTO;
import com.duoduo.jxc.entity.sales.SalesReturnDetail;
import com.duoduo.jxc.entity.sales.SalesReturnOrder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalesReturnOrderConverter {
    SalesReturnOrder toEntity(SalesReturnOrderDTO dto);
    SalesReturnOrderDTO toDTO(SalesReturnOrder entity);
    List<SalesReturnOrderDTO> toDTOList(List<SalesReturnOrder> entities);

    SalesReturnDetail toDetailEntity(SalesReturnDetailDTO dto);
    SalesReturnDetailDTO toDetailDTO(SalesReturnDetail entity);
    List<SalesReturnDetail> toDetailEntityList(List<SalesReturnDetailDTO> dtos);
    List<SalesReturnDetailDTO> toDetailDTOList(List<SalesReturnDetail> entities);
}
