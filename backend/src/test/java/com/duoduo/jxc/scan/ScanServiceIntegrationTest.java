package com.duoduo.jxc.scan;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.entity.Process;
import com.duoduo.jxc.entity.QrCode;
import com.duoduo.jxc.mapper.BundleMapper;
import com.duoduo.jxc.mapper.ProcessMapper;
import com.duoduo.jxc.service.QrCodeService;
import com.duoduo.jxc.service.ScanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class ScanServiceIntegrationTest {

    @Autowired
    private ScanService scanService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private BundleMapper bundleMapper;

    @Autowired
    private ProcessMapper processMapper;

    private Long testWorkerId = 1L;
    private Long testProcessId = 1L;

    @BeforeEach
    void setup() {
        List<Process> processes = processMapper.selectList(null);
        if (!processes.isEmpty()) {
            testProcessId = processes.get(0).getProcessId();
        }
    }

    @Test
    void scanWorkflow_manualScan_shouldCreateRecord() {
        String bundleNo = "TEST_MANUAL_" + System.currentTimeMillis();
        createTestBundle(bundleNo);

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundleNo);
        dto.setProcessId(testProcessId);
        dto.setWorkerId(testWorkerId);
        dto.setQuantity(10);

        ScanRecordDTO result = scanService.scan(dto);

        assertNotNull(result);
        assertNotNull(result.getRecordId());
        assertEquals(bundleNo, result.getBundleNo());
        assertEquals(testProcessId, result.getProcessId());
        assertEquals(testWorkerId, result.getWorkerId());
        assertEquals(10, result.getQuantity());
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void scanWorkflow_qrCodeScan_shouldCreateRecord() {
        String bundleNo = "TEST_QR_" + System.currentTimeMillis();
        Bundle bundle = createTestBundle(bundleNo);

        QrCode qrCode = qrCodeService.generateBundleQrCode(bundle.getBundleId(), bundleNo);

        ScanRecordDTO result = scanService.scanWithQrCode(
                qrCode.getQrCodeNo(),
                testWorkerId,
                testProcessId,
                5
        );

        assertNotNull(result);
        assertNotNull(result.getRecordId());
        assertEquals(bundleNo, result.getBundleNo());
    }

    @Test
    void scanWorkflow_duplicateScan_shouldThrow() {
        String bundleNo = "TEST_DUP_" + System.currentTimeMillis();
        createTestBundle(bundleNo);

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundleNo);
        dto.setProcessId(testProcessId);
        dto.setWorkerId(testWorkerId);
        dto.setQuantity(10);

        scanService.scan(dto);

        assertThrows(Exception.class, () -> scanService.scan(dto));
    }

    @Test
    void scanWorkflow_invalidQuantity_shouldThrow() {
        String bundleNo = "TEST_QTY_" + System.currentTimeMillis();
        createTestBundle(bundleNo);

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundleNo);
        dto.setProcessId(testProcessId);
        dto.setWorkerId(testWorkerId);
        dto.setQuantity(0);

        assertThrows(Exception.class, () -> scanService.scan(dto));
    }

    @Test
    void scanWorkflow_quantityExceedMax_shouldThrow() {
        String bundleNo = "TEST_MAX_QTY_" + System.currentTimeMillis();
        createTestBundle(bundleNo);

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundleNo);
        dto.setProcessId(testProcessId);
        dto.setWorkerId(testWorkerId);
        dto.setQuantity(10000);

        assertThrows(Exception.class, () -> scanService.scan(dto));
    }

    @Test
    void getTodayRecords_shouldReturnList() {
        String bundleNo = "TEST_TODAY_" + System.currentTimeMillis();
        createTestBundle(bundleNo);

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundleNo);
        dto.setProcessId(testProcessId);
        dto.setWorkerId(testWorkerId);
        dto.setQuantity(5);

        scanService.scan(dto);

        List<ScanRecordDTO> records = scanService.getTodayRecordsByWorker(testWorkerId);

        assertNotNull(records);
        assertFalse(records.isEmpty());
    }

    @Test
    void confirmRecord_shouldUpdateStatus() {
        String bundleNo = "TEST_CONFIRM_" + System.currentTimeMillis();
        createTestBundle(bundleNo);

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundleNo);
        dto.setProcessId(testProcessId);
        dto.setWorkerId(testWorkerId);
        dto.setQuantity(5);

        ScanRecordDTO result = scanService.scan(dto);

        scanService.confirm(result.getRecordId());

        ScanRecordDTO updated = scanService.getDetail(result.getRecordId());
        assertEquals("CONFIRMED", updated.getStatus());
        assertNotNull(updated.getConfirmAt());
    }

    @Test
    void rejectRecord_shouldUpdateStatus() {
        String bundleNo = "TEST_REJECT_" + System.currentTimeMillis();
        createTestBundle(bundleNo);

        ScanRecordDTO dto = new ScanRecordDTO();
        dto.setBundleNo(bundleNo);
        dto.setProcessId(testProcessId);
        dto.setWorkerId(testWorkerId);
        dto.setQuantity(5);

        ScanRecordDTO result = scanService.scan(dto);

        scanService.reject(result.getRecordId(), "测试拒绝");

        ScanRecordDTO updated = scanService.getDetail(result.getRecordId());
        assertEquals("REJECTED", updated.getStatus());
    }

    private Bundle createTestBundle(String bundleNo) {
        Bundle bundle = new Bundle();
        bundle.setBundleNo(bundleNo);
        bundle.setQuantity(100);
        bundle.setStatus("pending");
        bundleMapper.insert(bundle);
        return bundle;
    }
}
