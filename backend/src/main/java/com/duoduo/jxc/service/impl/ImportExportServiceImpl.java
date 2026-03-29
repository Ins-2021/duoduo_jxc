package com.duoduo.jxc.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.dto.importexport.*;
import com.duoduo.jxc.entity.*;
import com.duoduo.jxc.listener.*;
import com.duoduo.jxc.mapper.*;
import com.duoduo.jxc.service.ImportExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {

    private final ImportExportRecordMapper recordMapper;
    private final ProductSpuMapper productSpuMapper;
    private final ProductCategoryMapper categoryMapper;
    private final CustomerMapper customerMapper;
    private final SupplierMapper supplierMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final InventoryMapper inventoryMapper;
    private final ReceivableMapper receivableMapper;
    private final PayableMapper payableMapper;

    @Override
    public void downloadTemplate(String bizType, HttpServletResponse response) {
        String fileName = getTemplateFileName(bizType);
        String sheetName = getTemplateSheetName(bizType);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try {
            List<List<String>> head = getTemplateHead(bizType);
            List<List<Object>> data = getTemplateSampleData(bizType);
            
            EasyExcel.write(response.getOutputStream())
                    .head(head)
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (IOException e) {
            log.error("下载模板失败", e);
            throw new RuntimeException("下载模板失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importData(MultipartFile file, String bizType) throws Exception {
        ImportExportRecord record = new ImportExportRecord();
        record.setBizType(bizType);
        record.setOperation("import");
        record.setFileName(file.getOriginalFilename());
        record.setStatus(0);
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);

        try {
            switch (bizType) {
                case "product":
                    return importProductData(file, record);
                case "customer":
                    return importCustomerData(file, record);
                case "supplier":
                    return importSupplierData(file, record);
                default:
                    throw new IllegalArgumentException("不支持的导入类型: " + bizType);
            }
        } catch (Exception e) {
            record.setStatus(2);
            recordMapper.updateById(record);
            throw e;
        }
    }

    private ImportResultDTO importProductData(MultipartFile file, ImportExportRecord record) throws Exception {
        ProductImportListener listener = new ProductImportListener();
        EasyExcel.read(file.getInputStream(), ProductImportDTO.class, listener).sheet().doRead();
        
        List<ProductImportDTO> successList = listener.getSuccessList();
        List<ProductImportDTO> errorList = listener.getErrorList();
        
        int successCount = 0;
        List<ProductImportDTO> realErrorList = new ArrayList<>();
        
        Map<String, Long> categoryMap = getCategoryMap();
        Set<String> existingCodes = getExistingProductCodes();
        
        for (ProductImportDTO dto : successList) {
            if (existingCodes.contains(dto.getProductCode().trim())) {
                dto.setErrorMsg("商品编码已存在");
                realErrorList.add(dto);
                continue;
            }
            
            ProductSpu spu = new ProductSpu();
            spu.setProductCode(dto.getProductCode().trim());
            spu.setSpuName(dto.getProductName().trim());
            spu.setUnit(StringUtils.hasText(dto.getUnit()) ? dto.getUnit().trim() : "件");
            
            if (StringUtils.hasText(dto.getCategoryName())) {
                Long categoryId = categoryMap.get(dto.getCategoryName().trim());
                if (categoryId != null) {
                    spu.setCategoryId(categoryId);
                }
            }
            
            if (StringUtils.hasText(dto.getCostPrice())) {
                try {
                    spu.setPurchasePrice(new BigDecimal(dto.getCostPrice().trim()));
                } catch (NumberFormatException ignored) {
                }
            }
            if (StringUtils.hasText(dto.getSalePrice())) {
                try {
                    spu.setRetailPrice(new BigDecimal(dto.getSalePrice().trim()));
                } catch (NumberFormatException ignored) {
                }
            }
            
            spu.setStatus("启用".equals(dto.getStatus()) ? 1 : 0);
            spu.setCreateTime(LocalDateTime.now());
            
            productSpuMapper.insert(spu);
            successCount++;
            existingCodes.add(dto.getProductCode().trim());
        }
        
        realErrorList.addAll(errorList);
        
        record.setTotalCount(listener.getTotalCount());
        record.setSuccessCount(successCount);
        record.setErrorCount(realErrorList.size());
        record.setStatus(1);
        
        String errorFilePath = null;
        if (!realErrorList.isEmpty()) {
            errorFilePath = generateErrorFile(realErrorList, "product");
            record.setErrorFilePath(errorFilePath);
        }
        
        recordMapper.updateById(record);
        
        return ImportResultDTO.of(record.getRecordId(), listener.getTotalCount(), successCount, realErrorList.size(), errorFilePath);
    }

    private ImportResultDTO importCustomerData(MultipartFile file, ImportExportRecord record) throws Exception {
        CustomerImportListener listener = new CustomerImportListener();
        EasyExcel.read(file.getInputStream(), CustomerImportDTO.class, listener).sheet().doRead();
        
        List<CustomerImportDTO> successList = listener.getSuccessList();
        List<CustomerImportDTO> errorList = listener.getErrorList();
        
        int successCount = 0;
        List<CustomerImportDTO> realErrorList = new ArrayList<>();
        
        Set<String> existingCodes = getExistingCustomerCodes();
        
        for (CustomerImportDTO dto : successList) {
            if (existingCodes.contains(dto.getCustomerCode().trim())) {
                dto.setErrorMsg("客户编码已存在");
                realErrorList.add(dto);
                continue;
            }
            
            Customer customer = new Customer();
            customer.setCustomerName(dto.getCustomerName().trim());
            customer.setContactName(dto.getContactName());
            customer.setContactPhone(dto.getPhone());
            customer.setAddress(dto.getAddress());
            customer.setLevel("批发".equals(dto.getCustomerType()) ? "wholesale" : "retail");
            customer.setStatus("启用".equals(dto.getStatus()) ? 1 : 0);
            customer.setCreateTime(LocalDateTime.now());
            
            customerMapper.insert(customer);
            successCount++;
            existingCodes.add(dto.getCustomerCode().trim());
        }
        
        realErrorList.addAll(errorList);
        
        record.setTotalCount(listener.getTotalCount());
        record.setSuccessCount(successCount);
        record.setErrorCount(realErrorList.size());
        record.setStatus(1);
        
        String errorFilePath = null;
        if (!realErrorList.isEmpty()) {
            errorFilePath = generateCustomerErrorFile(realErrorList);
            record.setErrorFilePath(errorFilePath);
        }
        
        recordMapper.updateById(record);
        
        return ImportResultDTO.of(record.getRecordId(), listener.getTotalCount(), successCount, realErrorList.size(), errorFilePath);
    }

    private ImportResultDTO importSupplierData(MultipartFile file, ImportExportRecord record) throws Exception {
        SupplierImportListener listener = new SupplierImportListener();
        EasyExcel.read(file.getInputStream(), SupplierImportDTO.class, listener).sheet().doRead();
        
        List<SupplierImportDTO> successList = listener.getSuccessList();
        List<SupplierImportDTO> errorList = listener.getErrorList();
        
        int successCount = 0;
        List<SupplierImportDTO> realErrorList = new ArrayList<>();
        
        Set<String> existingCodes = getExistingSupplierCodes();
        
        for (SupplierImportDTO dto : successList) {
            if (existingCodes.contains(dto.getSupplierCode().trim())) {
                dto.setErrorMsg("供应商编码已存在");
                realErrorList.add(dto);
                continue;
            }
            
            Supplier supplier = new Supplier();
            supplier.setSupplierName(dto.getSupplierName().trim());
            supplier.setContactName(dto.getContactName());
            supplier.setContactPhone(dto.getPhone());
            supplier.setAddress(dto.getAddress());
            supplier.setStatus("启用".equals(dto.getStatus()) ? 1 : 0);
            supplier.setCreateTime(LocalDateTime.now());
            
            supplierMapper.insert(supplier);
            successCount++;
            existingCodes.add(dto.getSupplierCode().trim());
        }
        
        realErrorList.addAll(errorList);
        
        record.setTotalCount(listener.getTotalCount());
        record.setSuccessCount(successCount);
        record.setErrorCount(realErrorList.size());
        record.setStatus(1);
        
        String errorFilePath = null;
        if (!realErrorList.isEmpty()) {
            errorFilePath = generateSupplierErrorFile(realErrorList);
            record.setErrorFilePath(errorFilePath);
        }
        
        recordMapper.updateById(record);
        
        return ImportResultDTO.of(record.getRecordId(), listener.getTotalCount(), successCount, realErrorList.size(), errorFilePath);
    }

    @Override
    public void exportData(String bizType, Map<String, Object> params, HttpServletResponse response) {
        String fileName = getExportFileName(bizType);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try {
            switch (bizType) {
                case "salesOrder":
                    exportSalesOrder(params, response);
                    break;
                case "purchaseOrder":
                    exportPurchaseOrder(params, response);
                    break;
                case "inventory":
                    exportInventory(params, response);
                    break;
                case "receivable":
                    exportReceivable(params, response);
                    break;
                case "payable":
                    exportPayable(params, response);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的导出类型: " + bizType);
            }
        } catch (IOException e) {
            log.error("导出数据失败", e);
            throw new RuntimeException("导出数据失败: " + e.getMessage());
        }
    }

    private void exportSalesOrder(Map<String, Object> params, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SalesOrder::getCreateTime);
        
        List<SalesOrder> orders = salesOrderMapper.selectList(wrapper);
        
        List<SalesOrderExportDTO> exportList = orders.stream().map(order -> {
            SalesOrderExportDTO dto = new SalesOrderExportDTO();
            dto.setDocNo(order.getDocNo());
            dto.setDocDate(order.getDocDate() != null ? order.getDocDate().toString() : "");
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPaidAmount(order.getPaidAmount());
            dto.setRemark(order.getRemark());
            dto.setStatusText(getOrderStatusText(order.getStatus()));
            return dto;
        }).collect(Collectors.toList());
        
        EasyExcel.write(response.getOutputStream(), SalesOrderExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("销售订单")
                .doWrite(exportList);
    }

    private void exportPurchaseOrder(Map<String, Object> params, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        
        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(wrapper);
        
        List<PurchaseOrderExportDTO> exportList = orders.stream().map(order -> {
            PurchaseOrderExportDTO dto = new PurchaseOrderExportDTO();
            dto.setDocNo(order.getDocNo());
            dto.setDocDate(order.getDocDate() != null ? order.getDocDate().toString() : "");
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPaidAmount(order.getPaidAmount());
            dto.setRemark(order.getRemark());
            dto.setStatusText(getOrderStatusText(order.getStatus()));
            return dto;
        }).collect(Collectors.toList());
        
        EasyExcel.write(response.getOutputStream(), PurchaseOrderExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("采购订单")
                .doWrite(exportList);
    }

    private void exportInventory(Map<String, Object> params, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Inventory::getQty, 0);
        
        List<Inventory> inventories = inventoryMapper.selectList(wrapper);
        
        List<InventoryExportDTO> exportList = inventories.stream().map(inv -> {
            InventoryExportDTO dto = new InventoryExportDTO();
            dto.setQty(inv.getQty());
            dto.setAvailableQty(inv.getAvailableQty());
            dto.setLockedQty(inv.getLockedQty());
            return dto;
        }).collect(Collectors.toList());
        
        EasyExcel.write(response.getOutputStream(), InventoryExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("库存明细")
                .doWrite(exportList);
    }

    private void exportReceivable(Map<String, Object> params, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Receivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Receivable::getBillDate);
        
        List<Receivable> receivables = receivableMapper.selectList(wrapper);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ReceivableExportDTO> exportList = receivables.stream().map(r -> {
            ReceivableExportDTO dto = new ReceivableExportDTO();
            dto.setBillNo(r.getBillNo());
            dto.setCustomerName(r.getCustomerName());
            dto.setAmount(r.getAmount());
            dto.setReceivedAmount(r.getReceivedAmount());
            dto.setRemainingAmount(r.getRemainingAmount());
            dto.setBillDate(r.getBillDate() != null ? r.getBillDate().format(formatter) : "");
            dto.setDueDate(r.getDueDate() != null ? r.getDueDate().format(formatter) : "");
            dto.setStatusText(getReceivableStatusText(r.getStatus()));
            return dto;
        }).collect(Collectors.toList());
        
        EasyExcel.write(response.getOutputStream(), ReceivableExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("应收账款")
                .doWrite(exportList);
    }

    private void exportPayable(Map<String, Object> params, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Payable> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Payable::getBillDate);
        
        List<Payable> payables = payableMapper.selectList(wrapper);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<PayableExportDTO> exportList = payables.stream().map(p -> {
            PayableExportDTO dto = new PayableExportDTO();
            dto.setBillNo(p.getBillNo());
            dto.setSupplierName(p.getSupplierName());
            dto.setAmount(p.getAmount());
            dto.setPaidAmount(p.getPaidAmount());
            dto.setRemainingAmount(p.getRemainingAmount());
            dto.setBillDate(p.getBillDate() != null ? p.getBillDate().format(formatter) : "");
            dto.setDueDate(p.getDueDate() != null ? p.getDueDate().format(formatter) : "");
            dto.setStatusText(getPayableStatusText(p.getStatus()));
            return dto;
        }).collect(Collectors.toList());
        
        EasyExcel.write(response.getOutputStream(), PayableExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("应付账款")
                .doWrite(exportList);
    }

    @Override
    public ImportExportRecord getImportRecord(Long recordId) {
        return recordMapper.selectById(recordId);
    }

    private String getTemplateFileName(String bizType) {
        return switch (bizType) {
            case "product" -> "商品导入模板.xlsx";
            case "customer" -> "客户导入模板.xlsx";
            case "supplier" -> "供应商导入模板.xlsx";
            default -> "导入模板.xlsx";
        };
    }

    private String getTemplateSheetName(String bizType) {
        return switch (bizType) {
            case "product" -> "商品信息";
            case "customer" -> "客户信息";
            case "supplier" -> "供应商信息";
            default -> "数据";
        };
    }

    private String getExportFileName(String bizType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return switch (bizType) {
            case "salesOrder" -> "销售订单_" + timestamp + ".xlsx";
            case "purchaseOrder" -> "采购订单_" + timestamp + ".xlsx";
            case "inventory" -> "库存明细_" + timestamp + ".xlsx";
            case "receivable" -> "应收账款_" + timestamp + ".xlsx";
            case "payable" -> "应付账款_" + timestamp + ".xlsx";
            default -> "导出数据_" + timestamp + ".xlsx";
        };
    }

    private List<List<String>> getTemplateHead(String bizType) {
        return switch (bizType) {
            case "product" -> Arrays.asList(
                    Collections.singletonList("商品编码"), Collections.singletonList("商品名称"),
                    Collections.singletonList("商品类型"), Collections.singletonList("分类名称"),
                    Collections.singletonList("单位"), Collections.singletonList("成本价"),
                    Collections.singletonList("销售价"), Collections.singletonList("条码"),
                    Collections.singletonList("状态"));
            case "customer" -> Arrays.asList(
                    Collections.singletonList("客户编码"), Collections.singletonList("客户名称"),
                    Collections.singletonList("客户类型"), Collections.singletonList("联系人"),
                    Collections.singletonList("电话"), Collections.singletonList("地址"),
                    Collections.singletonList("销售员"), Collections.singletonList("状态"));
            case "supplier" -> Arrays.asList(
                    Collections.singletonList("供应商编码"), Collections.singletonList("供应商名称"),
                    Collections.singletonList("联系人"), Collections.singletonList("电话"),
                    Collections.singletonList("地址"), Collections.singletonList("状态"));
            default -> Collections.emptyList();
        };
    }

    private List<List<Object>> getTemplateSampleData(String bizType) {
        return switch (bizType) {
            case "product" -> Arrays.asList(
                    Arrays.asList("SP001", "测试商品1", "面料", "面料类", "件", "100", "150", "", "启用"),
                    Arrays.asList("SP002", "测试商品2", "辅料", "辅料类", "个", "50", "80", "", "启用"));
            case "customer" -> Arrays.asList(
                    Arrays.asList("C001", "测试客户1", "批发", "张三", "13800138000", "测试地址", "李四", "启用"),
                    Arrays.asList("C002", "测试客户2", "零售", "王五", "13900139000", "测试地址2", "", "启用"));
            case "supplier" -> Arrays.asList(
                    Arrays.asList("S001", "测试供应商1", "联系人1", "13800138001", "地址1", "启用"),
                    Arrays.asList("S002", "测试供应商2", "联系人2", "13900139002", "地址2", "启用"));
            default -> Collections.emptyList();
        };
    }

    private Map<String, Long> getCategoryMap() {
        List<ProductCategory> categories = categoryMapper.selectList(null);
        return categories.stream()
                .collect(Collectors.toMap(ProductCategory::getCategoryName, ProductCategory::getCategoryId, (a, b) -> a));
    }

    private Set<String> getExistingProductCodes() {
        List<ProductSpu> products = productSpuMapper.selectList(null);
        return products.stream()
                .map(ProductSpu::getProductCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> getExistingCustomerCodes() {
        return new HashSet<>();
    }

    private Set<String> getExistingSupplierCodes() {
        return new HashSet<>();
    }

    private String generateErrorFile(List<ProductImportDTO> errorList, String type) {
        return "/tmp/error_" + type + "_" + System.currentTimeMillis() + ".xlsx";
    }

    private String generateCustomerErrorFile(List<CustomerImportDTO> errorList) {
        return "/tmp/error_customer_" + System.currentTimeMillis() + ".xlsx";
    }

    private String generateSupplierErrorFile(List<SupplierImportDTO> errorList) {
        return "/tmp/error_supplier_" + System.currentTimeMillis() + ".xlsx";
    }

    private String getOrderStatusText(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 10 -> "草稿";
            case 20 -> "待审核";
            case 30 -> "执行中";
            case 40 -> "已完成";
            case 90 -> "已作废";
            default -> "未知";
        };
    }

    private String getReceivableStatusText(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "未收款";
            case 1 -> "部分核销";
            case 2 -> "已核销";
            default -> "未知";
        };
    }

    private String getPayableStatusText(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "未付款";
            case 1 -> "部分核销";
            case 2 -> "已核销";
            default -> "未知";
        };
    }
}
