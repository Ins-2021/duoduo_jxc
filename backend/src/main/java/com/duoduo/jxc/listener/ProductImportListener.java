package com.duoduo.jxc.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.duoduo.jxc.dto.importexport.ProductImportDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductImportListener extends AnalysisEventListener<ProductImportDTO> {

    private final List<ProductImportDTO> successList = new ArrayList<>();
    private final List<ProductImportDTO> errorList = new ArrayList<>();
    private int rowNum = 1;

    @Override
    public void invoke(ProductImportDTO data, AnalysisContext context) {
        rowNum++;
        data.setRowNum(rowNum);
        
        StringBuilder errorMsg = new StringBuilder();
        
        if (data.getProductCode() == null || data.getProductCode().trim().isEmpty()) {
            errorMsg.append("商品编码不能为空;");
        }
        if (data.getProductName() == null || data.getProductName().trim().isEmpty()) {
            errorMsg.append("商品名称不能为空;");
        }
        if (data.getProductType() == null || data.getProductType().trim().isEmpty()) {
            errorMsg.append("商品类型不能为空;");
        } else if (!isValidProductType(data.getProductType())) {
            errorMsg.append("商品类型无效(面料/辅料/成品);");
        }
        
        if (errorMsg.length() > 0) {
            data.setErrorMsg(errorMsg.toString());
            errorList.add(data);
        } else {
            successList.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    private boolean isValidProductType(String type) {
        return "面料".equals(type) || "辅料".equals(type) || "成品".equals(type);
    }

    public List<ProductImportDTO> getSuccessList() {
        return successList;
    }

    public List<ProductImportDTO> getErrorList() {
        return errorList;
    }

    public int getTotalCount() {
        return successList.size() + errorList.size();
    }
}
