package com.duoduo.jxc.service;

import com.duoduo.jxc.entity.QrCode;

import java.awt.image.BufferedImage;

public interface QrCodeService {

    QrCode generateBundleQrCode(Long bundleId, String bundleNo);

    QrCode generateQrCode(String refType, Long refId, String content);

    QrCode getByQrCodeNo(String qrCodeNo);

    QrCode getByRef(String refType, Long refId);

    BufferedImage generateQrImage(String content, int size);

    String parseQrContent(String qrContent);

    boolean verifySignature(String qrContent, String signature);

    void incrementScanCount(Long qrId);

    void incrementPrintCount(Long qrId);
}
