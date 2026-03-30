package com.duoduo.jxc.converter;

import com.duoduo.jxc.dto.fabric.*;
import com.duoduo.jxc.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FabricConverter {
    FabricConverter INSTANCE = Mappers.getMapper(FabricConverter.class);

    Fabric toEntity(FabricDTO dto);
    FabricDTO toDTO(Fabric entity);
    List<FabricDTO> toDTOList(List<Fabric> list);

    FabricInbound toEntity(FabricInboundDTO dto);
    FabricInboundDTO toInboundDTO(FabricInbound entity);
    List<FabricInboundDTO> toInboundDTOList(List<FabricInbound> list);

    FabricRequisition toEntity(FabricRequisitionDTO dto);
    FabricRequisitionDTO toRequisitionDTO(FabricRequisition entity);
    List<FabricRequisitionDTO> toRequisitionDTOList(List<FabricRequisition> list);

    FabricInventory toEntity(FabricInventoryDTO dto);
    FabricInventoryDTO toInventoryDTO(FabricInventory entity);
    List<FabricInventoryDTO> toInventoryDTOList(List<FabricInventory> list);
}
