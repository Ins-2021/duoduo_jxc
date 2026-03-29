package com.duoduo.jxc.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.duoduo.jxc.dto.importexport.SupplierImportDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SupplierImportListener extends AnalysisEventListener<SupplierImportDTO> {

    private final List<SupplierImportDTO> successList = new ArrayList<>();
    private final List<SupplierImportDTO> errorList = new ArrayList<>();
    private int rowNum = 1;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public void invoke(SupplierImportDTO data, AnalysisContext context) {
        rowNum++;
        data.setRowNum(rowNum);
        
        StringBuilder errorMsg = new StringBuilder();
        
        if (data.getSupplierCode() == null || data.getSupplierCode().trim().isEmpty()) {
            errorMsg.append("供应商编码不能为空;");
        }
        if (data.getSupplierName() == null || data.getSupplierName().trim().isEmpty()) {
            errorMsg.append("供应商名称不能为空;");
        }
        if (data.getPhone() != null && !data.getPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(data.getPhone().trim()).matches()) {
                errorMsg.append("电话格式不正确;");
            }
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

    public List<SupplierImportDTO> getSuccessList() {
        return successList;
    }

    public List<SupplierImportDTO> getErrorList() {
        return errorList;
    }

    public int getTotalCount() {
        return successList.size() + errorList.size();
    }
}
