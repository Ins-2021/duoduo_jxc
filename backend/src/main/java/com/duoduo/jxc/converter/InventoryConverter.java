package com.duoduo.jxc.converter;

import com.duoduo.jxc.dto.inventory.*;
import com.duoduo.jxc.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryConverter {
    InventoryConverter INSTANCE = Mappers.getMapper(InventoryConverter.class);

    Inventory toEntity(InventoryDTO dto);
    InventoryDTO toDTO(Inventory entity);
    List<InventoryDTO> toDTOList(List<Inventory> list);

    TransferOrder toEntity(TransferOrderDTO dto);
    TransferOrderDTO toTransferOrderDTO(TransferOrder entity);
    List<TransferOrderDTO> toTransferOrderDTOList(List<TransferOrder> list);

    TransferOrderDetail toEntity(TransferOrderDetailDTO dto);
    TransferOrderDetailDTO toTransferOrderDetailDTO(TransferOrderDetail entity);
    List<TransferOrderDetailDTO> toTransferOrderDetailDTOList(List<TransferOrderDetail> list);

    InventoryCheck toEntity(InventoryCheckDTO dto);
    InventoryCheckDTO toInventoryCheckDTO(InventoryCheck entity);
    List<InventoryCheckDTO> toInventoryCheckDTOList(List<InventoryCheck> list);

    InventoryCheckDetail toEntity(InventoryCheckDetailDTO dto);
    InventoryCheckDetailDTO toInventoryCheckDetailDTO(InventoryCheckDetail entity);
    List<InventoryCheckDetailDTO> toInventoryCheckDetailDTOList(List<InventoryCheckDetail> list);

    AssemblyOrder toEntity(AssemblyOrderDTO dto);
    AssemblyOrderDTO toAssemblyOrderDTO(AssemblyOrder entity);
    List<AssemblyOrderDTO> toAssemblyOrderDTOList(List<AssemblyOrder> list);

    AssemblyOrderDetail toEntity(AssemblyOrderDetailDTO dto);
    AssemblyOrderDetailDTO toAssemblyOrderDetailDTO(AssemblyOrderDetail entity);
    List<AssemblyOrderDetailDTO> toAssemblyOrderDetailDTOList(List<AssemblyOrderDetail> list);

    StockInOut toEntity(StockInOutDTO dto);
    StockInOutDTO toStockInOutDTO(StockInOut entity);
    List<StockInOutDTO> toStockInOutDTOList(List<StockInOut> list);

    StockInOutDetail toEntity(StockInOutDetailDTO dto);
    StockInOutDetailDTO toStockInOutDetailDTO(StockInOutDetail entity);
    List<StockInOutDetailDTO> toStockInOutDetailDTOList(List<StockInOutDetail> list);

    InventoryAlert toEntity(InventoryAlertDTO dto);
    InventoryAlertDTO toInventoryAlertDTO(InventoryAlert entity);
    List<InventoryAlertDTO> toInventoryAlertDTOList(List<InventoryAlert> list);
}
