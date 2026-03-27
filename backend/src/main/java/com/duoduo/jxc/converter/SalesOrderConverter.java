package com.duoduo.jxc.converter;

import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.dto.sales.SalesOrderDetailDTO;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.SalesOrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalesOrderConverter {
    SalesOrderConverter INSTANCE = Mappers.getMapper(SalesOrderConverter.class);

    SalesOrder toEntity(SalesOrderDTO dto);
    SalesOrderDTO toDTO(SalesOrder entity);
    List<SalesOrderDTO> toDTOList(List<SalesOrder> list);

    SalesOrderDetail toDetailEntity(SalesOrderDetailDTO dto);
    SalesOrderDetailDTO toDetailDTO(SalesOrderDetail entity);
    List<SalesOrderDetailDTO> toDetailDTOList(List<SalesOrderDetail> list);
    List<SalesOrderDetail> toDetailEntityList(List<SalesOrderDetailDTO> list);
}
