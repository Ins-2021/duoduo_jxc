package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.inventory.BarcodeDTO;
import com.duoduo.jxc.dto.inventory.BarcodeGenerateRequest;
import com.duoduo.jxc.dto.inventory.BarcodeRuleDTO;
import com.duoduo.jxc.entity.Barcode;

import java.util.List;

public interface BarcodeService extends IService<Barcode> {

    /**
     * 生成条码
     */
    List<BarcodeDTO> generateBarcode(BarcodeGenerateRequest request);

    /**
     * 解析条码内容，返回结构化信息
     */
    BarcodeDTO parseBarcode(String barcodeContent);

    /**
     * 标记条码已打印
     */
    void printBarcode(Long barcodeId);

    /**
     * 批量标记已打印
     */
    void printBarcodeBatch(List<Long> barcodeIds);
}
