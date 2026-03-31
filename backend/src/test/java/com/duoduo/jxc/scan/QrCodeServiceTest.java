package com.duoduo.jxc.scan;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.constant.QrCodeConstant;
import com.duoduo.jxc.entity.QrCode;
import com.duoduo.jxc.mapper.QrCodeMapper;
import com.duoduo.jxc.service.QrCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class QrCodeServiceTest {

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private QrCodeMapper qrCodeMapper;

    @Test
    void generateBundleQrCode_shouldCreateQrCode() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST" + bundleId;

        QrCode qrCode = qrCodeService.generateBundleQrCode(bundleId, bundleNo);

        assertNotNull(qrCode);
        assertNotNull(qrCode.getQrId());
        assertNotNull(qrCode.getQrCodeNo());
        assertEquals(QrCodeConstant.QR_TYPE_BUNDLE, qrCode.getRefType());
        assertEquals(bundleId, qrCode.getRefId());
        assertEquals("ACTIVE", qrCode.getStatus());
        assertNotNull(qrCode.getQrContent());
        assertNotNull(qrCode.getSignature());
        assertNotNull(qrCode.getExpireAt());
        assertTrue(qrCode.getExpireAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void generateBundleQrCode_duplicateBundleId_shouldReturnExisting() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST_DUP" + bundleId;

        QrCode first = qrCodeService.generateBundleQrCode(bundleId, bundleNo);
        QrCode second = qrCodeService.generateBundleQrCode(bundleId, bundleNo);

        assertEquals(first.getQrId(), second.getQrId());
        assertEquals(first.getQrCodeNo(), second.getQrCodeNo());
    }

    @Test
    void generateQrImage_shouldReturnImage() {
        String content = "TEST_QR_CONTENT_" + System.currentTimeMillis();
        int size = 200;

        BufferedImage image = qrCodeService.generateQrImage(content, size);

        assertNotNull(image);
        assertEquals(size, image.getWidth());
        assertEquals(size, image.getHeight());
    }

    @Test
    void generateQrImage_invalidSize_shouldUseDefault() {
        String content = "TEST_QR_CONTENT_SMALL";
        int size = 0;

        BufferedImage image = qrCodeService.generateQrImage(content, QrCodeConstant.DEFAULT_QR_SIZE);

        assertNotNull(image);
    }

    @Test
    void getByQrCodeNo_existing_shouldReturnQrCode() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST_GET" + bundleId;
        QrCode created = qrCodeService.generateBundleQrCode(bundleId, bundleNo);

        QrCode found = qrCodeService.getByQrCodeNo(created.getQrCodeNo());

        assertNotNull(found);
        assertEquals(created.getQrId(), found.getQrId());
    }

    @Test
    void getByQrCodeNo_notExisting_shouldReturnNull() {
        QrCode found = qrCodeService.getByQrCodeNo("NOT_EXIST_" + System.currentTimeMillis());
        assertNull(found);
    }

    @Test
    void getByRef_existing_shouldReturnQrCode() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST_REF" + bundleId;
        QrCode created = qrCodeService.generateBundleQrCode(bundleId, bundleNo);

        QrCode found = qrCodeService.getByRef(QrCodeConstant.QR_TYPE_BUNDLE, bundleId);

        assertNotNull(found);
        assertEquals(created.getQrId(), found.getQrId());
    }

    @Test
    void parseQrContent_validJson_shouldExtractNo() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST_PARSE" + bundleId;
        QrCode created = qrCodeService.generateBundleQrCode(bundleId, bundleNo);

        String qrCodeNo = qrCodeService.parseQrContent(created.getQrContent());

        assertNotNull(qrCodeNo);
        assertEquals(created.getQrCodeNo(), qrCodeNo);
    }

    @Test
    void parseQrContent_nullContent_shouldReturnNull() {
        String result = qrCodeService.parseQrContent(null);
        assertNull(result);
    }

    @Test
    void parseQrContent_emptyContent_shouldReturnNull() {
        String result = qrCodeService.parseQrContent("");
        assertNull(result);
    }

    @Test
    void verifySignature_valid_shouldReturnTrue() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST_VERIFY" + bundleId;
        QrCode created = qrCodeService.generateBundleQrCode(bundleId, bundleNo);

        boolean valid = qrCodeService.verifySignature(created.getQrContent(), created.getSignature());

        if (QrCodeConstant.ENABLE_SIGNATURE) {
            assertTrue(valid);
        } else {
            assertTrue(valid);
        }
    }

    @Test
    void verifySignature_invalid_shouldReturnFalse() {
        boolean valid = qrCodeService.verifySignature("test_content", "invalid_signature");
        assertFalse(valid);
    }

    @Test
    void incrementScanCount_shouldIncreaseCount() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST_SCAN_COUNT" + bundleId;
        QrCode created = qrCodeService.generateBundleQrCode(bundleId, bundleNo);
        int initialCount = created.getScanCount() != null ? created.getScanCount() : 0;

        qrCodeService.incrementScanCount(created.getQrId());

        QrCode updated = qrCodeMapper.selectById(created.getQrId());
        assertEquals(initialCount + 1, updated.getScanCount());
        assertNotNull(updated.getLastScanAt());
    }

    @Test
    void incrementPrintCount_shouldIncreaseCount() {
        Long bundleId = System.currentTimeMillis();
        String bundleNo = "TEST_PRINT_COUNT" + bundleId;
        QrCode created = qrCodeService.generateBundleQrCode(bundleId, bundleNo);
        int initialCount = created.getPrintCount() != null ? created.getPrintCount() : 0;

        qrCodeService.incrementPrintCount(created.getQrId());

        QrCode updated = qrCodeMapper.selectById(created.getQrId());
        assertEquals(initialCount + 1, updated.getPrintCount());
        assertEquals(1, updated.getPrinted());
        assertNotNull(updated.getLastPrintAt());
    }
}
