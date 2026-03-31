package com.duoduo.jxc.service.impl;

import com.duoduo.jxc.constant.QrCodeConstant;
import com.duoduo.jxc.service.DuplicateCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateCheckServiceImpl implements DuplicateCheckService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public boolean tryAcquireScanLock(String bundleNo, Long workerId, Long processId) {
        String lockKey = buildScanLockKey(bundleNo, workerId, processId);
        try {
            Boolean success = stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", QrCodeConstant.LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(success)) {
                log.info("scanLockAcquired bundleNo={} workerId={} processId={}", bundleNo, workerId, processId);
                return true;
            }
            log.warn("scanLockFailed bundleNo={} workerId={} processId={}", bundleNo, workerId, processId);
            return false;
        } catch (Exception e) {
            log.error("scanLockError bundleNo={} workerId={} processId={}", bundleNo, workerId, processId, e);
            return false;
        }
    }

    @Override
    public void releaseScanLock(String bundleNo, Long workerId, Long processId) {
        String lockKey = buildScanLockKey(bundleNo, workerId, processId);
        try {
            stringRedisTemplate.delete(lockKey);
            log.info("scanLockReleased bundleNo={} workerId={} processId={}", bundleNo, workerId, processId);
        } catch (Exception e) {
            log.error("scanLockReleaseError bundleNo={} workerId={} processId={}", bundleNo, workerId, processId, e);
        }
    }

    @Override
    public boolean markScanned(Long bundleId, Long workerId, Long processId) {
        String markKey = buildScanMarkKey(bundleId, workerId, processId);
        try {
            Boolean success = stringRedisTemplate.opsForValue()
                    .setIfAbsent(markKey, "1", QrCodeConstant.SCAN_MARK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            log.error("scanMarkError bundleId={} workerId={} processId={}", bundleId, workerId, processId, e);
            return false;
        }
    }

    @Override
    public boolean isScannedToday(Long bundleId, Long workerId, Long processId) {
        String markKey = buildScanMarkKey(bundleId, workerId, processId);
        try {
            String value = stringRedisTemplate.opsForValue().get(markKey);
            return value != null;
        } catch (Exception e) {
            log.error("scanMarkCheckError bundleId={} workerId={} processId={}", bundleId, workerId, processId, e);
            return false;
        }
    }

    @Override
    public boolean tryAcquireBundleLock(Long bundleId) {
        String lockKey = buildBundleLockKey(bundleId);
        try {
            Boolean success = stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", QrCodeConstant.BUNDLE_LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(success)) {
                log.info("bundleLockAcquired bundleId={}", bundleId);
                return true;
            }
            log.warn("bundleLockFailed bundleId={}", bundleId);
            return false;
        } catch (Exception e) {
            log.error("bundleLockError bundleId={}", bundleId, e);
            return false;
        }
    }

    @Override
    public void releaseBundleLock(Long bundleId) {
        String lockKey = buildBundleLockKey(bundleId);
        try {
            stringRedisTemplate.delete(lockKey);
            log.info("bundleLockReleased bundleId={}", bundleId);
        } catch (Exception e) {
            log.error("bundleLockReleaseError bundleId={}", bundleId, e);
        }
    }

    private String buildScanLockKey(String bundleNo, Long workerId, Long processId) {
        return QrCodeConstant.REDIS_KEY_SCAN_LOCK_PREFIX + bundleNo + ":" + workerId + ":" + processId;
    }

    private String buildScanMarkKey(Long bundleId, Long workerId, Long processId) {
        String date = LocalDate.now().format(DATE_FORMATTER);
        return QrCodeConstant.REDIS_KEY_SCAN_MARK_PREFIX + date + ":" + bundleId + ":" + workerId + ":" + processId;
    }

    private String buildBundleLockKey(Long bundleId) {
        return QrCodeConstant.REDIS_KEY_BUNDLE_LOCK_PREFIX + bundleId;
    }
}
