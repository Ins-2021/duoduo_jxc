package com.duoduo.jxc.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.duoduo.jxc.dto.importexport.ProductCategoryImportDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ProductCategoryImportListener implements ReadListener<ProductCategoryImportDTO> {
    
    private static final int BATCH_COUNT = 100;
    
    private final List<ProductCategoryImportDTO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private final List<ProductCategoryImportDTO> successList = ListUtils.newArrayList();
    private final List<ProductCategoryImportDTO> failList = ListUtils.newArrayList();
    
    @Override
    public void invoke(ProductCategoryImportDTO data, AnalysisContext context) {
        log.info("解析到一条数据: {}", data);
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList.clear();
        }
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成！");
    }
    
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
    }
    
    public List<ProductCategoryImportDTO> getSuccessList() {
        return successList;
    }
    
    public List<ProductCategoryImportDTO> getFailList() {
        return failList;
    }
    
    public void addSuccess(ProductCategoryImportDTO dto) {
        successList.add(dto);
    }
    
    public void addFail(ProductCategoryImportDTO dto) {
        failList.add(dto);
    }
    
    public List<ProductCategoryImportDTO> getCachedDataList() {
        return cachedDataList;
    }
}
