package com.duoduo.jxc.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.duoduo.jxc.dto.importexport.CustomerImportDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CustomerImportListener extends AnalysisEventListener<CustomerImportDTO> {

    private final List<CustomerImportDTO> successList = new ArrayList<>();
    private final List<CustomerImportDTO> errorList = new ArrayList<>();
    private int rowNum = 1;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public void invoke(CustomerImportDTO data, AnalysisContext context) {
        rowNum++;
        data.setRowNum(rowNum);
        
        StringBuilder errorMsg = new StringBuilder();
        
        if (data.getCustomerCode() == null || data.getCustomerCode().trim().isEmpty()) {
            errorMsg.append("客户编码不能为空;");
        }
        if (data.getCustomerName() == null || data.getCustomerName().trim().isEmpty()) {
            errorMsg.append("客户名称不能为空;");
        }
        if (data.getPhone() != null && !data.getPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(data.getPhone().trim()).matches()) {
                errorMsg.append("电话格式不正确;");
            }
        }
        if (data.getCustomerType() != null && !data.getCustomerType().trim().isEmpty()) {
            if (!"批发".equals(data.getCustomerType()) && !"零售".equals(data.getCustomerType())) {
                errorMsg.append("客户类型无效(批发/零售);");
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

    public List<CustomerImportDTO> getSuccessList() {
        return successList;
    }

    public List<CustomerImportDTO> getErrorList() {
        return errorList;
    }

    public int getTotalCount() {
        return successList.size() + errorList.size();
    }
}
