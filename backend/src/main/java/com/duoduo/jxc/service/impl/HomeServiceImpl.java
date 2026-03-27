package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.duoduo.jxc.dto.home.HomeOverviewDTO;
import com.duoduo.jxc.entity.FinanceAccount;
import com.duoduo.jxc.entity.Inventory;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.PurchaseOrder;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.mapper.FinanceAccountMapper;
import com.duoduo.jxc.mapper.InventoryMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.PurchaseOrderMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final SalesOrderMapper salesOrderMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final InventoryMapper inventoryMapper;
    private final ProductSkuMapper productSkuMapper;
    private final FinanceAccountMapper financeAccountMapper;

    @Override
    public HomeOverviewDTO getOverview() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        BigDecimal todaySalesAmount = sumSalesActualAmount(today);
        BigDecimal yesterdaySalesAmount = sumSalesActualAmount(yesterday);
        int todaySalesCount = countSalesOrder(today);
        int yesterdaySalesCount = countSalesOrder(yesterday);

        BigDecimal todayCashIn = sumSalesPaidAmount(today);
        BigDecimal yesterdayCashIn = sumSalesPaidAmount(yesterday);

        long customerCount = countDistinctSalesCustomer();
        long supplierCount = countDistinctPurchaseSupplier();

        List<Inventory> inventoryList = listInventory();
        Map<Long, ProductSku> skuMap = buildSkuMap(inventoryList);
        long inventoryQtyTotal = sumInventoryQty(inventoryList);
        BigDecimal inventoryAmountTotal = sumInventoryAmount(inventoryList, skuMap);
        long inventoryWarnCount = countInventoryWarn(inventoryList, skuMap);
        long shelfLifeWarnCount = 0L;

        BigDecimal receivableAmount = sumReceivableAmount();
        BigDecimal payableAmount = sumPayableAmount();
        BigDecimal accountBalanceTotal = sumAccountBalance();

        HomeOverviewDTO dto = new HomeOverviewDTO();
        dto.setTodayGrossProfit(BigDecimal.ZERO);
        dto.setYesterdayGrossProfit(BigDecimal.ZERO);
        dto.setTodaySalesAmount(todaySalesAmount);
        dto.setYesterdaySalesAmount(yesterdaySalesAmount);
        dto.setTodaySalesCount(todaySalesCount);
        dto.setYesterdaySalesCount(yesterdaySalesCount);
        dto.setTodayCashIn(todayCashIn);
        dto.setYesterdayCashIn(yesterdayCashIn);

        dto.setCustomerCount(customerCount);
        dto.setSupplierCount(supplierCount);
        dto.setInventoryQtyTotal(inventoryQtyTotal);
        dto.setInventoryAmountTotal(inventoryAmountTotal);
        dto.setInventoryWarnCount(inventoryWarnCount);
        dto.setShelfLifeWarnCount(shelfLifeWarnCount);
        dto.setReceivableAmount(receivableAmount);
        dto.setPayableAmount(payableAmount);
        dto.setAssetsTotal(accountBalanceTotal.add(inventoryAmountTotal).add(receivableAmount).subtract(payableAmount));

        fillSeries(dto, today);
        return dto;
    }

    private void fillSeries(HomeOverviewDTO dto, LocalDate today) {
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i));
        }

        List<String> dateLabels = new ArrayList<>();
        List<Integer> salesCounts = new ArrayList<>();
        List<BigDecimal> income = new ArrayList<>();
        List<BigDecimal> expense = new ArrayList<>();

        for (LocalDate d : dates) {
            dateLabels.add(d.toString());
            salesCounts.add(countSalesOrder(d));
            income.add(sumSalesPaidAmount(d));
            expense.add(sumPurchasePaidAmount(d));
        }

        dto.setOverviewDates(dateLabels);
        dto.setOverviewSalesOrderCount(salesCounts);
        dto.setCashflowDates(dateLabels);
        dto.setCashflowIncome(income);
        dto.setCashflowExpense(expense);
    }

    private BigDecimal sumSalesActualAmount(LocalDate docDate) {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(actual_amount),0) AS val")
                .eq("doc_date", docDate)
                .ne("status", 90)
                .ne("order_type", 3);
        return getBigDecimal(wrapperResultFirstMap(salesOrderMapper.selectMaps(wrapper)), "val");
    }

    private int countSalesOrder(LocalDate docDate) {
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesOrder::getDocDate, docDate)
                .ne(SalesOrder::getStatus, 90)
                .ne(SalesOrder::getOrderType, 3);
        Long cnt = salesOrderMapper.selectCount(wrapper);
        return cnt == null ? 0 : Math.toIntExact(cnt);
    }

    private BigDecimal sumSalesPaidAmount(LocalDate docDate) {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(paid_amount),0) AS val")
                .eq("doc_date", docDate)
                .ne("status", 90)
                .ne("order_type", 3);
        return getBigDecimal(wrapperResultFirstMap(salesOrderMapper.selectMaps(wrapper)), "val");
    }

    private BigDecimal sumPurchasePaidAmount(LocalDate docDate) {
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(paid_amount),0) AS val")
                .eq("doc_date", docDate)
                .ne("status", 90);
        return getBigDecimal(wrapperResultFirstMap(purchaseOrderMapper.selectMaps(wrapper)), "val");
    }

    private long countDistinctSalesCustomer() {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(COUNT(DISTINCT customer_id),0) AS val")
                .ne("status", 90);
        return getLong(wrapperResultFirstMap(salesOrderMapper.selectMaps(wrapper)), "val");
    }

    private long countDistinctPurchaseSupplier() {
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(COUNT(DISTINCT supplier_id),0) AS val")
                .ne("status", 90);
        return getLong(wrapperResultFirstMap(purchaseOrderMapper.selectMaps(wrapper)), "val");
    }

    private List<Inventory> listInventory() {
        return inventoryMapper.selectList(new LambdaQueryWrapper<>());
    }

    private Map<Long, ProductSku> buildSkuMap(List<Inventory> inventoryList) {
        if (inventoryList == null || inventoryList.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> skuIds = inventoryList.stream()
                .map(Inventory::getSkuId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (skuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ProductSku> skuList = productSkuMapper.selectBatchIds(skuIds);
        Map<Long, ProductSku> skuMap = new HashMap<>();
        for (ProductSku sku : skuList) {
            skuMap.put(sku.getSkuId(), sku);
        }
        return skuMap;
    }

    private long sumInventoryQty(List<Inventory> inventoryList) {
        if (inventoryList == null || inventoryList.isEmpty()) {
            return 0L;
        }
        return inventoryList.stream()
                .map(Inventory::getQty)
                .filter(qty -> qty != null)
                .mapToLong(Integer::longValue)
                .sum();
    }

    private BigDecimal sumInventoryAmount(List<Inventory> inventoryList, Map<Long, ProductSku> skuMap) {
        if (inventoryList == null || inventoryList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Inventory inventory : inventoryList) {
            if (inventory.getQty() == null || inventory.getQty() <= 0) {
                continue;
            }
            ProductSku sku = skuMap.get(inventory.getSkuId());
            BigDecimal purchasePrice = sku == null || sku.getPurchasePrice() == null ? BigDecimal.ZERO : sku.getPurchasePrice();
            total = total.add(purchasePrice.multiply(BigDecimal.valueOf(inventory.getQty())));
        }
        return total;
    }

    private long countInventoryWarn(List<Inventory> inventoryList, Map<Long, ProductSku> skuMap) {
        if (inventoryList == null || inventoryList.isEmpty()) {
            return 0L;
        }
        return inventoryList.stream()
                .filter(inventory -> {
                    Integer qty = inventory.getQty();
                    ProductSku sku = skuMap.get(inventory.getSkuId());
                    Integer warningQty = sku == null ? null : sku.getWarningQty();
                    if (qty == null) {
                        return false;
                    }
                    if (warningQty == null) {
                        return qty <= 0;
                    }
                    return qty <= warningQty;
                })
                .count();
    }

    private BigDecimal sumAccountBalance() {
        QueryWrapper<FinanceAccount> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(balance),0) AS val")
                .eq("status", 1);
        return getBigDecimal(wrapperResultFirstMap(financeAccountMapper.selectMaps(wrapper)), "val");
    }

    private BigDecimal sumReceivableAmount() {
        QueryWrapper<SalesOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(COALESCE(actual_amount,0) - COALESCE(paid_amount,0)),0) AS val")
                .ne("status", 90)
                .ne("order_type", 3);
        return getBigDecimal(wrapperResultFirstMap(salesOrderMapper.selectMaps(wrapper)), "val");
    }

    private BigDecimal sumPayableAmount() {
        QueryWrapper<PurchaseOrder> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(COALESCE(total_amount,0) - COALESCE(paid_amount,0)),0) AS val")
                .ne("status", 90);
        return getBigDecimal(wrapperResultFirstMap(purchaseOrderMapper.selectMaps(wrapper)), "val");
    }

    private Map<String, Object> wrapperResultFirstMap(List<Map<String, Object>> maps) {
        return maps == null || maps.isEmpty() ? null : maps.get(0);
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object v = getValue(map, key);
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        return new BigDecimal(String.valueOf(v));
    }

    private long getLong(Map<String, Object> map, String key) {
        Object v = getValue(map, key);
        if (v == null) return 0L;
        if (v instanceof Number) return ((Number) v).longValue();
        return Long.parseLong(String.valueOf(v));
    }

    private Object getValue(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object v = map.get(key);
        if (v != null) return v;
        v = map.get(key.toUpperCase());
        if (v != null) return v;
        return map.get(key.toLowerCase());
    }
}
