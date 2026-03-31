package com.duoduo.jxc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.data.WarehouseDTO;
import com.duoduo.jxc.dto.data.WarehouseQuery;
import com.duoduo.jxc.dto.inventory.InventoryCheckDTO;
import com.duoduo.jxc.dto.inventory.InventoryDTO;
import com.duoduo.jxc.dto.inventory.InventoryQuery;
import com.duoduo.jxc.dto.inventory.StockInOutDTO;
import com.duoduo.jxc.entity.Inventory;
import com.duoduo.jxc.entity.StockInOut;
import com.duoduo.jxc.entity.Warehouse;
import com.duoduo.jxc.enums.StockInOutStatusEnum;
import com.duoduo.jxc.mapper.InventoryMapper;
import com.duoduo.jxc.service.InventoryCheckService;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.StockInOutService;
import com.duoduo.jxc.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final StockInOutService stockInOutService;
    private final InventoryCheckService inventoryCheckService;
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:warehouse:view')")
    public Result<PageResult<Warehouse>> pageQuery(WarehouseQuery query) {
        return Result.success(warehouseService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:warehouse:view')")
    public Result<Warehouse> getDetail(@PathVariable Long id) {
        return Result.success(warehouseService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('data:warehouse:add')")
    public Result<Long> create(@RequestBody WarehouseDTO dto) {
        return Result.success(warehouseService.create(dto));
    }

    @PutMapping
    @PreAuthorize("@perm.has('data:warehouse:edit')")
    public Result<Void> update(@RequestBody WarehouseDTO dto) {
        warehouseService.update(dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@perm.has('data:warehouse:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody WarehouseDTO dto) {
        warehouseService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

    // ========== 小程序端接口 ==========

    /**
     * 仓库概览（小程序）
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalWarehouses", warehouseService.pageQuery(new WarehouseQuery()).getTotal());
        return Result.success(overview);
    }

    /**
     * 入库（小程序）
     */
    @PostMapping("/in")
    public Result<Long> inbound(@RequestBody StockInOutDTO dto) {
        dto.setType(1);
        return Result.success(stockInOutService.create(dto));
    }

    /**
     * 出库（小程序）
     */
    @PostMapping("/out")
    public Result<Long> outbound(@RequestBody StockInOutDTO dto) {
        dto.setType(2);
        return Result.success(stockInOutService.create(dto));
    }

    /**
     * 库存列表（小程序）
     */
    @GetMapping("/stock/list")
    public Result<PageResult<InventoryDTO>> getStockList(@RequestParam(defaultValue = "1") int pageNum,
                                                          @RequestParam(defaultValue = "20") int pageSize,
                                                          @RequestParam(required = false) Long warehouseId) {
        InventoryQuery query = new InventoryQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setWarehouseId(warehouseId);
        return Result.success(inventoryService.pageQuery(query));
    }

    /**
     * 盘点任务列表（小程序）
     */
    @GetMapping("/inventory/list")
    public Result<PageResult<InventoryCheckDTO>> getInventoryTasks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageQuery query = new PageQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(inventoryCheckService.pageList(query));
    }

    /**
     * 提交盘点（小程序）
     */
    @PostMapping("/inventory")
    public Result<Long> submitInventory(@RequestBody InventoryCheckDTO dto) {
        return Result.success(inventoryCheckService.create(dto));
    }

    /**
     * 今日出入库记录（小程序）
     */
    @GetMapping("/records/today")
    public Result<List<StockInOutDTO>> getTodayRecords(@RequestParam(required = false) Integer type) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        LambdaQueryWrapper<StockInOut> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(StockInOut::getCreateTime, startOfDay)
               .lt(StockInOut::getCreateTime, endOfDay)
               .eq(StockInOut::getStatus, StockInOutStatusEnum.APPROVED.getValue());
        if (type != null) {
            wrapper.eq(StockInOut::getType, type);
        }
        wrapper.orderByDesc(StockInOut::getCreateTime);
        
        List<StockInOut> records = stockInOutService.list(wrapper);
        List<StockInOutDTO> result = records.stream().map(entity -> {
            StockInOutDTO dto = new StockInOutDTO();
            dto.setInOutId(entity.getInOutId());
            dto.setBillNo(entity.getBillNo());
            dto.setWarehouseId(entity.getWarehouseId());
            dto.setWarehouseName(entity.getWarehouseName());
            dto.setType(entity.getType());
            dto.setStatus(entity.getStatus());
            dto.setCreateTime(entity.getCreateTime());
            return dto;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 待处理列表（小程序）
     */
    @GetMapping("/pending")
    public Result<List<Map<String, Object>>> getPendingList() {
        LambdaQueryWrapper<StockInOut> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInOut::getStatus, StockInOutStatusEnum.DRAFT.getValue())
               .orderByAsc(StockInOut::getCreateTime);
        
        List<StockInOut> pendingRecords = stockInOutService.list(wrapper);
        List<Map<String, Object>> result = pendingRecords.stream().map(entity -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", entity.getInOutId());
            item.put("billNo", entity.getBillNo());
            item.put("type", entity.getType() == 1 ? "入库" : "出库");
            item.put("warehouseName", entity.getWarehouseName());
            item.put("createTime", entity.getCreateTime());
            item.put("status", "待审核");
            return item;
        }).collect(Collectors.toList());
        return Result.success(result);
    }
}
