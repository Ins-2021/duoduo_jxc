package com.duoduo.jxc.scan;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.ScanRecordDTO;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.mapper.BundleMapper;
import com.duoduo.jxc.service.ScanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class ScanConcurrencyTest {

    @Autowired
    private ScanService scanService;

    @Autowired
    private BundleMapper bundleMapper;

    private Long testProcessId = 1L;
    private int threadCount = 100;

    @Test
    void concurrentScan_sameBundle_sameWorker_sameProcess_shouldPreventDuplicate() throws InterruptedException {
        String bundleNo = "CONCURRENT_DUP_" + System.currentTimeMillis();
        Bundle bundle = createTestBundle(bundleNo);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int workerNum = i;
            executor.submit(() -> {
                try {
                    ScanRecordDTO dto = new ScanRecordDTO();
                    dto.setBundleNo(bundleNo);
                    dto.setProcessId(testProcessId);
                    dto.setWorkerId(1L);
                    dto.setQuantity(1);

                    scanService.scan(dto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, successCount.get(), "只应有一个线程成功扫码");
        assertEquals(threadCount - 1, failCount.get(), "其他线程应被防重机制阻止");
    }

    @Test
    void concurrentScan_sameBundle_differentWorkers_shouldAllSucceed() throws InterruptedException {
        String bundleNo = "CONCURRENT_WORKER_" + System.currentTimeMillis();
        Bundle bundle = createTestBundle(bundleNo);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final long workerId = i + 1;
            executor.submit(() -> {
                try {
                    ScanRecordDTO dto = new ScanRecordDTO();
                    dto.setBundleNo(bundleNo);
                    dto.setProcessId(testProcessId);
                    dto.setWorkerId(workerId);
                    dto.setQuantity(1);

                    scanService.scan(dto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount, successCount.get(), "不同工人的扫码应全部成功");
        assertEquals(0, failCount.get());
    }

    @Test
    void concurrentScan_sameBundle_sameWorker_differentProcesses_shouldAllSucceed() throws InterruptedException {
        String bundleNo = "CONCURRENT_PROCESS_" + System.currentTimeMillis();
        Bundle bundle = createTestBundle(bundleNo);

        int processCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(processCount);
        CountDownLatch latch = new CountDownLatch(processCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < processCount; i++) {
            final long processId = i + 1;
            executor.submit(() -> {
                try {
                    ScanRecordDTO dto = new ScanRecordDTO();
                    dto.setBundleNo(bundleNo);
                    dto.setProcessId(processId);
                    dto.setWorkerId(1L);
                    dto.setQuantity(1);

                    scanService.scan(dto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(processCount, successCount.get(), "不同工序的扫码应全部成功");
        assertEquals(0, failCount.get());
    }

    @Test
    void concurrentScan_differentBundles_sameWorker_shouldAllSucceed() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<String> bundleNos = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            String bundleNo = "CONCURRENT_BUNDLE_" + i + "_" + System.currentTimeMillis();
            bundleNos.add(bundleNo);
            createTestBundle(bundleNo);
        }

        for (int i = 0; i < threadCount; i++) {
            final String bundleNo = bundleNos.get(i);
            executor.submit(() -> {
                try {
                    ScanRecordDTO dto = new ScanRecordDTO();
                    dto.setBundleNo(bundleNo);
                    dto.setProcessId(testProcessId);
                    dto.setWorkerId(1L);
                    dto.setQuantity(1);

                    scanService.scan(dto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount, successCount.get(), "不同扎包的扫码应全部成功");
        assertEquals(0, failCount.get());
    }

    private Bundle createTestBundle(String bundleNo) {
        Bundle bundle = new Bundle();
        bundle.setBundleNo(bundleNo);
        bundle.setQuantity(1000);
        bundle.setStatus("pending");
        bundleMapper.insert(bundle);
        return bundle;
    }
}
