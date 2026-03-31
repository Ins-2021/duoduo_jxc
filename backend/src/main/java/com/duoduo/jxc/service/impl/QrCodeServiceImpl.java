package com.duoduo.jxc.service.impl;

import com.alibaba.fastjson2.JSON;
import com.duoduo.jxc.constant.QrCodeConstant;
import com.duoduo.jxc.entity.QrCode;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.mapper.QrCodeMapper;
import com.duoduo.jxc.service.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final QrCodeMapper qrCodeMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QrCode generateBundleQrCode(Long bundleId, String bundleNo) {
        QrCode existing = qrCodeMapper.selectByRef(QrCodeConstant.QR_TYPE_BUNDLE, bundleId);
        if (existing != null) {
            log.info("qrCodeExists bundleId={} qrId={}", bundleId, existing.getQrId());
            return existing;
        }

        return generateQrCode(QrCodeConstant.QR_TYPE_BUNDLE, bundleId, bundleNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QrCode generateQrCode(String refType, Long refId, String content) {
        String qrCodeNo = generateQrCodeNo(refType);

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("no", qrCodeNo);
        contentMap.put("type", refType);
        contentMap.put("refId", refId);
        contentMap.put("content", content);
        contentMap.put("version", QrCodeConstant.QR_VERSION);
        contentMap.put("timestamp", System.currentTimeMillis());

        String qrContentJson = JSON.toJSONString(contentMap);
        String signature = generateSignature(qrContentJson);

        QrCode qrCode = new QrCode();
        qrCode.setQrCodeNo(qrCodeNo);
        qrCode.setRefType(refType);
        qrCode.setRefId(refId);
        qrCode.setQrContent(qrContentJson);
        qrCode.setSignature(signature);
        qrCode.setStatus("ACTIVE");
        qrCode.setPrinted(0);
        qrCode.setPrintCount(0);
        qrCode.setScanCount(0);
        qrCode.setExpireAt(LocalDateTime.now().plusHours(QrCodeConstant.QR_EXPIRE_HOURS));

        qrCodeMapper.insert(qrCode);
        log.info("qrCodeGenerated qrCodeNo={} refType={} refId={}", qrCodeNo, refType, refId);

        return qrCode;
    }

    @Override
    public QrCode getByQrCodeNo(String qrCodeNo) {
        if (qrCodeNo == null || qrCodeNo.isEmpty()) {
            return null;
        }
        return qrCodeMapper.selectByQrCodeNo(qrCodeNo);
    }

    @Override
    public QrCode getByRef(String refType, Long refId) {
        return qrCodeMapper.selectByRef(refType, refId);
    }

    @Override
    public BufferedImage generateQrImage(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, QrCodeConstant.QR_CHARSET);
            hints.put(EncodeHintType.MARGIN, QrCodeConstant.QR_MARGIN);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (Exception e) {
            log.error("qrImageGenerateError content={}", content, e);
            throw new BusinessException(BizCode.SYSTEM_ERROR, "二维码图片生成失败");
        }
    }

    @Override
    public String parseQrContent(String qrContent) {
        if (qrContent == null || qrContent.isEmpty()) {
            return null;
        }
        try {
            Map<String, Object> contentMap = JSON.parseObject(qrContent, Map.class);
            return (String) contentMap.get("no");
        } catch (Exception e) {
            log.error("qrContentParseError qrContent={}", qrContent, e);
            return null;
        }
    }

    @Override
    public boolean verifySignature(String qrContent, String signature) {
        if (!QrCodeConstant.ENABLE_SIGNATURE) {
            return true;
        }
        if (qrContent == null || signature == null) {
            return false;
        }
        String calculatedSignature = generateSignature(qrContent);
        return signature.equals(calculatedSignature);
    }

    @Override
    public void incrementScanCount(Long qrId) {
        qrCodeMapper.incrementScanCount(qrId);
        log.info("qrCodeScanCountIncremented qrId={}", qrId);
    }

    @Override
    public void incrementPrintCount(Long qrId) {
        qrCodeMapper.incrementPrintCount(qrId);
        log.info("qrCodePrintCountIncremented qrId={}", qrId);
    }

    private String generateQrCodeNo(String refType) {
        String date = LocalDateTime.now().format(DATETIME_FORMATTER);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "QR" + refType.charAt(0) + date + uuid;
    }

    private String generateSignature(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance(QrCodeConstant.SIGNATURE_ALGORITHM);
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.substring(0, Math.min(QrCodeConstant.SIGNATURE_LENGTH, sb.length()));
        } catch (Exception e) {
            log.error("signatureGenerateError", e);
            return "";
        }
    }
}
